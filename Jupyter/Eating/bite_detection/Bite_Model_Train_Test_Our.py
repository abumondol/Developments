
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split
import tensorflow as tf


# In[35]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_steven_lab_utils as mslabu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
importlib.reload(mcnnu)


# In[20]:


win_size, step_size = 5*16, 2
axis_count, feature_count = 3, 32
gx_th_bite, gx_th_free = -0.25, 0
print("Win size:", win_size, ", Step size:", step_size, ', gx_th bite, free:', gx_th_bite, gx_th_free)


# In[21]:


def get_mid_gx(ds, indices, win_size):
    count = len(indices)
    x = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]+win_size//2
        x[i] = ds[subj][sess][ix, -3]                
    return x    


# In[22]:


def get_variance_accel(ds, indices, win_size):   
    count = len(indices)
    v = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        v[i] = np.sum(np.var(ds[subj][sess][ix:ix+win_size, 1:4], axis=0))                
    return v


# In[51]:


def get_window_data(ds, fs, indices, win_size, offset=0):
    count = len(indices)
    w = np.zeros((count, win_size, 3))    
    features = np.zeros((count, 32))    
    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]        
        w[i, :, :] = ds[subj][sess][ix:ix+win_size, -3:]
        features[i, :] = fs[subj][sess][ix//2, 1:]
        assert ix == fs[subj][sess][ix//2, 0]
    
    return w, features


# In[24]:


def get_labels_lab(indices, a, win_size):
    a = a[a[:, 3]!=2, :] # right hand only    
    wcount, acount = len(indices), len(a)  
    
    si = indices
    q1 = si + win_size//4
    mi = si + 2*win_size//4    
    q3 = si + 3*win_size//4    
    
    labels=np.zeros((wcount,))    
    for i in range(acount):         
        if a[i, 2]==1: #bite
            ix = a[i, 0]
            cond = (q1<=ix) & (ix<=q3)
            label = 1
        else:#sip
            ix1 = a[i, 0]
            ix2 = a[i, 1]
            cond = (mi>=ix1) & (mi<=ix2)
            label = 2
        
        labels[cond] = label
    return np.array(labels)


# In[25]:


def get_labels_free(indices, a, win_size):    
    wcount, acount = len(indices), len(a)      
    mi = indices + win_size//2    
    
    labels=np.zeros((wcount,))    
    for i in range(acount):                 
        ix1 = a[i, 0]
        ix2 = a[i, 1]
        cond = (mi>=ix1) & (mi<=ix2)
        labels[cond] = a[i, 2]
        
    return labels.astype(int)
    


# In[26]:


def get_all_indices_lab(exclude_subj=-1):
    all_indices = np.zeros((0, 4))
    for subj in range(len(dlab)):
        if subj==exclude_subj:
            continue
        for sess in range(len(dlab[subj])):
            d = ds[subj][sess]
            a = annots[subj][sess]

            count = (len(d)-win_size)//step_size + 1
            indices = np.array(list(range(count)))*2
            labels = get_labels_lab(indices, a, win_size)
            labels[labels==2] = 0

            ix = np.zeros((count, 4))
            ix[:, 0] = subj
            ix[:, 1] = sess
            ix[:, 2] = indices
            ix[:, 3] = labels
            all_indices = np.concatenate((all_indices, ix))

    all_indices = all_indices.astype(int)
    assert np.sum(all_indices[:, -1]>1) == 0
    assert np.sum(all_indices[:, -1]<0) == 0
    print("All indices shape, neg, bite:", len(all_indices), np.sum(all_indices[:, -1]==0), np.sum(all_indices[:, -1]==1))
    return all_indices


# In[27]:


def get_subject_indices_free(subj):
    subj_indices = np.zeros((0, 4))    
    for sess in range(len(dfree[subj])):
        d = ds[subj][sess]
        a = annots[subj][sess]
        
        count = (len(d)-win_size)//step_size + 1
        indices = np.array(list(range(count)))*2
        labels = get_labels_free(indices, a, win_size)
                
        ix = np.zeros((count, 4))
        ix[:, 0] = subj
        ix[:, 1] = sess
        ix[:, 2] = indices
        ix[:, 3] = labels
        subj_indices = np.concatenate((subj_indices, ix))        
    
    subj_indices = subj_indices.astype(int)        
    return all_indices


# In[28]:


def get_all_indices_free(exclude_subj=-1):
    all_indices = np.zeros((0, 4))    
    for subj in range(len(dfree)):
        if subj == exclude_subj:
            continue            
        ix = get_subject_indices_free(subj)
        all_indices = np.concatenate((all_indices, ix))
            
    all_indices = all_indices.astype(int)    
    return all_indices


# In[29]:


subj, num_epochs, train_test = 100, 50, 'train'
if 'C:' not in mfileu.get_path():    
    subj, num_epochs, train_test = int(sys.argv[1]), int(sys.argv[2]), sys.argv[3]
    
assert train_test in ['train', 'test_bite', 'retrain_o', 'retrain_p', 'test_free']
params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.25


# In[30]:


if train_test=='train':
    dlab = mfileu.read_file('data', 'lab_data_steven.pkl')
    ds, _, annots = mslabu.separate_right_left_annots(dlab)
    fs = mfileu.read_file('features', 'lab_features_steven_right.pkl')
else:
    ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
    annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
    fs = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[47]:


def train_test_model(train_indices, val_indices, params, model_path_dest=None, model_path_src=None, test_indices=[]):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']
    print("****** Learning rate ", learning_rate)
    
    #print_out, sys.stdout = sys.stdout, open(os.devnull, 'w')    
    
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, win_size, axis_count], name="x")    
    features = tf.placeholder(tf.float32, [None, feature_count], name="features")
    y = tf.placeholder(tf.float32, [None, 1], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    cnn_out = mcnnu.gravity_conv_net(x, name="gravity_conv_net")
    lstm_out_fw, lstm_out_bw = mlstmu.multi_layer_biLSTM(cnn_out, batch_size=batch_size, n_hidden=32, n_layer=1)
    lstm_out = tf.concat([lstm_out_fw[:, -1, :], lstm_out_bw[:, 0, :]], axis =1)
    print("Lstm out shapes(fw, bw): ", lstm_out_fw.get_shape().as_list(), lstm_out_bw.get_shape().as_list())
    print("Lstm out shape final: ", lstm_out.get_shape().as_list())
    
    mlp_in = tf.concat([features, lstm_out], axis =1)
    print("MLP Input shape final: ", mlp_in.get_shape().as_list())
    drop_out = tf.nn.dropout(mlp_in, keep_prob=keep_prob, name="dropout_0")
    
    for i in range(1, 6):
        mlp = mdenseu.fc_layer(drop_out, 64, name="mlp_"+str(i), activation='relu')
        drop_out = tf.nn.dropout(mlp, keep_prob=keep_prob, name="dropout_"+str(i))
        
    logits_bite = mdenseu.fc_layer(drop_out, 1, name="logits_bite")    
    print("logit bite shape in out: ", drop_out.get_shape().as_list(), logits_bite.get_shape().as_list() )
    
    #for i in range(6, 11):
    #    mlp = mdenseu.fc_layer(drop_out, 64, name="mlp_"+str(i), activation='relu')
    #    drop_out = tf.nn.dropout(mlp, keep_prob=keep_prob, name="dropout_"+str(i))
    
    #logits_free = mdenseu.fc_layer(drop_out, 1, name="logits_free")    
    #print("logit free shape in, out: ", drop_out.get_shape().as_list(), logits_free.get_shape().as_list() )
        
    prediction_bite = tf.nn.sigmoid(logits_bite, name="prediction_bite")
    correct_prediction_bite = tf.equal(tf.greater(prediction_bite, 0.5), tf.equal(y,1), name="correct_prediction_bite")
    accuracy_bite = tf.reduce_mean(tf.cast(correct_prediction_bite, tf.float32), name="accuracy_bite")
    
    #prediction_free = tf.nn.sigmoid(logits_free, name="prediction_free")
    #correct_prediction_free = tf.equal(tf.greater(prediction_free, 0.5), tf.equal(y,1), name="correct_prediction_free")
    #accuracy_free = tf.reduce_mean(tf.cast(correct_prediction_free, tf.float32), name="accuracy_free")
        
    loss_op_bite = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits_bite, labels=y), name="loss_op_bite")        
    #loss_op_free = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits_free, labels=y), name="loss_op_free")        
    
    train_step_bite = tf.train.AdamOptimizer(learning_rate).minimize(loss_op_bite, name="train_step_bite")
    #train_step_free = tf.train.AdamOptimizer(learning_rate).minimize(loss_op_free, name="train_step_free")

    sess = tf.Session()
    
    #sys.stdout = print_out
    ########## Train and then save the model ########################
    if len(train_indices)>0:
        assert train_test in ['train', 'retrain_o', 'retrain_p']
        if train_test=='train':                        
            sess.run(tf.global_variables_initializer())
            train_step, loss_op, accuracy, prediction = train_step_bite, loss_op_bite, accuracy_bite, prediction_bite
            print("*********** Global variable initialized for training")
        else:            
            saver = tf.train.Saver()
            saver.restore(sess, model_path_src+'/model')
            print(" ********* Model Loaded for retrain!")
            #train_step, loss_op, accuracy, prediction = train_step_free, loss_op_free, accuracy_free, prediction_free
            
        train_indices, _ = mclfu.adjust_for_batch_size(train_indices, train_indices, batch_size)

        train_count = len(train_indices)
        for epoch in range(num_epochs):
            print("Epoch:", epoch)
            total_loss, total_acc = 0, 0
            for ix in range(0, train_count, batch_size):                                            
                batch_x, batch_features = get_window_data(ds, fs, train_indices[ix:ix+batch_size], win_size)                 
                batch_y = train_indices[ix:ix+batch_size, -1].reshape((-1,1))                 
                sess.run(train_step, feed_dict={x:batch_x, features:batch_features, y:batch_y, keep_prob:keep_prob_val})                

                loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, y:batch_y, features:batch_features, keep_prob:1.0})        
                total_loss+= loss*batch_size
                total_acc += acc*batch_size                
            print('  Train loss: {:.4f}, acc: {:.4f}'.format(total_loss/train_count, total_acc/train_count))

            val_count = len(val_indices)
            if val_count>0:            
                val_indices, _ = mclfu.adjust_for_batch_size(val_indices, val_indices, batch_size)
                total_loss, total_acc = 0, 0
                for ix in range(0, val_count, batch_size):                
                    batch_x, batch_features = get_window_data(ds, fs, val_indices[ix:ix+batch_size], win_size)
                    batch_y = val_indices[ix:ix+batch_size, -1].reshape((-1,1))                      
                    loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, y:batch_y, features:batch_features, keep_prob:1.0})                
                    total_loss+= loss*batch_size
                    total_acc += acc*batch_size                
                print('  Val loss: {:.4f}, acc: {:.4f}'.format(total_loss/val_count, total_acc/val_count))

        print('!!!!!!!!!!!!!!! Optimization Finished !!!!!!!!!!!!!!!!!')
        
        res = []
        test_count_original = len(test_indices)        
        if test_count_original>0:            
            test_indices, _ = mclfu.adjust_for_batch_size(test_indices, test_indices, batch_size)
            test_count = len(test_indices)
            res = np.zeros((test_count, 1))

            for ix in range(0, test_count, batch_size):                
                batch_x, batch_features = get_window_data(ds, fs, test_indices[ix:ix+batch_size], win_size)
                batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
                pred = sess.run([prediction], feed_dict={x: batch_x, y:batch_y, features:batch_features, keep_prob:1.0})            
                res[ix:ix+batch_size, 0] = np.array(pred).reshape((-1, ))

            res = res[:test_count_original, :]       
        
        if model_path_dest:
            saver = tf.train.Saver()            
            mfileu.create_directory(model_path_dest)
            saver.save(sess, model_path_dest+'/model')    
            print("Model Saved!")
        
    ########## Restore the model and then Test  ########################
    else:
        saver = tf.train.Saver()
        saver.restore(sess, model_path_src+'/model')
        print("Model Loaded for test!")
        
        test_count_original = len(test_indices)        
        test_indices, _ = mclfu.adjust_for_batch_size(test_indices, test_indices, batch_size)
        test_count = len(test_indices)
        res = np.zeros((test_count, 1))
        
        for ix in range(0, test_count, batch_size):                
            batch_x, batch_features = get_window_data(ds, fs, test_indices[ix:ix+batch_size], win_size)
            batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
            pred = sess.run([prediction], feed_dict={x: batch_x, y:batch_y, keep_prob:1.0})            
            res[ix:ix+batch_size, 0] = np.array(pred).reshape((-1, ))
        
        res = res[:test_count_original, :]        
        
    sess.close() 
    return res
        


# In[48]:


print("=============================  Train/Test: {} =============================".format(train_test))
print("\n============ Subject, Epochs, Win Size: {}, {}, {} =============".format(subj, num_epochs, win_size))


# In[49]:


path = mfileu.get_path()
model_folder_src = path+'/our_bite_models'
model_folder_dest = path+'/our_bite_models'
result_folder = 'our_test_proba_retrain_personal'


# In[52]:


if train_test == 'train':
    print("Training.....")    
    indices = get_all_indices_lab(exclude_subj=subj)
    
    assert np.sum(indices[:, 0]==subj) == 0    
    assert np.sum(indices[:, -1]>1) == 0
    assert np.sum(indices[:, -1]<0) == 0
    print("Train Indices summary after subject filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
    
    gx = get_mid_gx(ds, indices, win_size=win_size)
    indices = indices[gx<=gx_th_bite]    
    print("Train Indices summary after gx filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
    
    indices = shuffle(indices)    
    train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    model_path_dest = "our_bite_models/subj_"+str(subj)
    train_test_model(train_indices, val_indices, params, model_path_dest=model_path_dest)  


# In[ ]:


if train_test == 'retrain_o':
    print("Re-Training.....")
    params['learning_rate'] = 0.0001
    
    indices = get_all_indices_free(exclude_subj=subj)
    assert np.sum(indices[:,0]==subj)==0
    
    indices[indices[:,-1]==2, -1] = 1
    indices[indices[:,-1]==3, -1] = 0
    assert np.sum((indices[:, -1]>1) | (indices[:, -1]<0)) == 0    
    print("Train Indices summary total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

    gx = get_mid_gx(ds, indices, win_size=win_size)
    indices = indices[gx<=gx_th]    
    print("Train Indices summary after gx filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

    indices = shuffle(indices)    
    train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))

    test_indices = get_subject_indices_free(subj)
    print("Test Indices summary total, neg, pos:", len(test_indices), np.sum(test_indices[:, -1]==0), np.sum(test_indices[:, -1]==1))
    
    gx = get_mid_gx(ds, test_indices, win_size=win_size)
    test_indices = test_indices[gx<=gx_th]    
    print("Test Indices summary after gx filter total, neg, pos:", len(test_indices), np.sum(test_indices[:, -1]==0), np.sum(test_indices[:, -1]==1))
    
    src_subj = subj+2 if subj<5 else 100            
    model_path_src = "our_bite_models/subj_"+str(src_subj) 
    model_path_dest = "our_free_models_general/subj_"+str(subj)
    pred = train_test_model(train_indices, val_indices, params, model_path_dest=model_path_dest, model_path_src=model_path_src, test_indices=test_indices)

    print("Prediction shape: ", pred.shape, np.sum(pred), np.sum(pred>=0.5))            
    assert len(pred)==len(test_indices)
    pred = np.concatenate((test_indices[:, :3], pred), axis=1)
    print("Prediction shape: ", pred.shape, np.sum(pred[:, 1]), np.sum(pred[:, 1]>=0.5))
    
    mfileu.write_file('our_free_results_general', 'window_free_'+str(subj)+".pkl", pred)


# In[ ]:


if train_test == 'retrain_p':
    print("Re-Training.....")
    params['learning_rate'] = 0.0001
    
    subj_indices = get_subject_indices_free(subj)
    assert np.sum(subj_indices[:,0]!=subj)==0
    print("Total subject indices: ", subj_indices.shape)
    
    for sess in range(len(ds[subj])):        
        indices = subj_indices[subj_indices[:,1]!=sess]

        indices[indices[:,-1]==2, -1] = 1
        indices[indices[:,-1]==3, -1] = 0
        assert np.sum((indices[:, -1]>1) | (indices[:, -1]<0)) == 0    
        print("Train Indices summary total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

        gx = get_mid_gx(ds, indices, win_size=win_size)
        indices = indices[gx<=gx_th]    
        print("Train Indices summary after gx filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))

        indices = shuffle(indices)    
        train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
        print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))

        gx = get_mid_gx(ds, subj_indices, win_size=win_size)
        test_indices = subj_indices[gx<=gx_th]    
        print("Test Indices summary after gx filter total, neg, pos:", len(test_indices), np.sum(test_indices[:, -1]==0), np.sum(test_indices[:, -1]==1))

        model_path_src = "our_bite_models_general/subj_"+str(subj) 
        model_path_dest = "our_free_models_personal/subj_"+str(subj)
        pred = train_test_model(train_indices, val_indices, params, model_path_dest=model_path_dest, model_path_src=model_path_src, test_indices=test_indices)

        print("Prediction shape: ", pred.shape, np.sum(pred), np.sum(pred>=0.5))            
        assert len(pred)==len(test_indices)
        pred = np.concatenate((test_indices[:, :3], pred), axis=1)
        print("Prediction shape: ", pred.shape, np.sum(pred[:, 1]), np.sum(pred[:, 1]>=0.5))

        mfileu.write_file('our_free_results_personal', 'window_free_'+str(subj)+"_"+str(subj)+".pkl", pred)


# In[ ]:


#train_test = 'test'
if train_test=='test':
    print("Testing.....")    
        
    for subj in range(11):
        indices = get_subject_indices_free(subj)
        print("Subj, sess, indices shape: ", subj, sess, indices.shape)

        gx = get_mid_gx(ds, indices, win_size=win_size)
        indices = indices[gx<=gx_th]    
        print("After gx filter indices shape: ", indices.shape)

        src_subj = subj+2 if subj<5 else 100            
        model_path_src = "our_bite_models/subj_"+str(src_subj)            
        pred = train_test_model([], indices, params, )            
        print("Prediction shape: ", pred.shape, np.sum(pred), np.sum(pred>=0.5))            
        assert len(pred)==len(indices)

        pred = pred.reshape((-1, 1))
        pred = np.concatenate((ix, pred), axis=1)
        print("Prediction shape: ", pred.shape, np.sum(pred[:, 1]), np.sum(pred[:, 1]>=0.5))            

        mfileu.write_file('our_bite_results', 'bite_free_'+str(subj)+".pkl", pred)

