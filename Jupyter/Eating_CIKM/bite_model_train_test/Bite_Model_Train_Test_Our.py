
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


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_steven_lab_utils as mslabu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
#importlib.reload(biteu)


# In[3]:


axis_count, label_shape_1 = 6, 1 
win_size, step_size, feature_count = 5*16, 8, 32
var_min, var_max, gx_th = 1, 25, -0.25
print("Win size:", win_size, ", Step size:", step_size, ", var min:", var_min, ", var max:", var_max, ', gx_th:', gx_th)


# In[4]:


def get_window_data(ds, indices, win_size, offset=0):
    count = len(indices)
    w = np.zeros((count, win_size, 6))    
    features = np.zeros((count, 32))    
    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        accel = ds[subj][sess][ix:ix+win_size, 1:4]
        #grav = ds[subj][sess][ix:ix+win_size, -3:]
        gyro = ds[subj][sess][ix:ix+win_size, 4:7]
        w[i, :, :] = np.concatenate((accel, gyro), axis=1)        
    
    return w, features


# In[5]:


subj, num_epochs, train_test = 0, 1, 'test_lab'
if 'C:' not in mfileu.get_path():    
    subj, num_epochs, train_test = int(sys.argv[1]), int(sys.argv[2]), sys.argv[3]
    
assert train_test in ['train', 'test_lab', 'test_free']
params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.5


# In[6]:


def train_test_model(ds, train_indices, test_indices, params, model_path_dest=None, model_path_src=None):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']
    print("****** Learning rate ", learning_rate)
    
    print_out, sys.stdout = sys.stdout, open(os.devnull, 'w')    
    
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, win_size, axis_count], name="x")    
    #features = tf.placeholder(tf.float32, [None, feature_count], name="features")
    y = tf.placeholder(tf.float32, [None, label_shape_1], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    cnn_out = mcnnu.all_sensor_net(x, name="all_sensor_net")
    lstm_out_fw, lstm_out_bw = mlstmu.multi_layer_biLSTM(cnn_out, batch_size=batch_size, n_hidden=64, n_layer=1)
    lstm_out = tf.concat([lstm_out_fw[:, -1, :], lstm_out_bw[:, 0, :]], axis =1)
    
    print("Lstm out shapes(fw, bw): ", lstm_out_fw.get_shape().as_list(), lstm_out_bw.get_shape().as_list())
    print("FW LSTM out shape:", lstm_out_fw[:, -1, :].get_shape().as_list())
    print("BW LSTM out shape:", lstm_out_bw[:, -1, :].get_shape().as_list())    
    print("Lstm out shape final: ", lstm_out.get_shape().as_list())
    
    drop_layer = tf.nn.dropout(lstm_out, keep_prob=keep_prob, name="dropout1")
    #fc_layer= mdenseu.fc_layer(drop_layer1, 64, name="Dense_1", activation='relu')    
    #drop_layer = tf.nn.dropout(fc_layer, keep_prob=keep_prob, name="dropout2")
    print("Drop layer shape: ",drop_layer.get_shape().as_list())
    logits = mdenseu.fc_layer(drop_layer, 1, name="Logits")    
    
    print("Logit shape: ",logits.get_shape().as_list())
    prediction = tf.nn.sigmoid(logits, name="prediction")
    correct_prediction = tf.equal(tf.greater(prediction, 0.5), tf.equal(y,1), name="correct_prediction")
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")
    
    loss_op = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")        
    train_step = tf.train.AdamOptimizer(learning_rate).minimize(loss_op, name="train_step")

    sess = tf.Session()
    
    sys.stdout = print_out
    ########## Train and then save the model ########################
    if len(train_indices)>0:                
        sess.run(tf.global_variables_initializer())    
            
        train_indices, _ = mclfu.adjust_for_batch_size(train_indices, train_indices, batch_size)

        train_count = len(train_indices)
        for epoch in range(num_epochs):
            print("Epoch:", epoch)
            total_loss, total_acc = 0, 0
            for ix in range(0, train_count, batch_size):                                            
                batch_x, batch_features = get_window_data(ds, train_indices[ix:ix+batch_size], win_size)                 
                batch_y = train_indices[ix:ix+batch_size, -1].reshape((-1,1))                 
                sess.run(train_step, feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})                

                loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})        
                total_loss+= loss*batch_size
                total_acc += acc*batch_size                
            print('  Train loss: {:.4f}, acc: {:.4f}'.format(total_loss/train_count, total_acc/train_count))

            test_count = len(test_indices)
            if test_count>0:            
                test_indices, _ = mclfu.adjust_for_batch_size(test_indices, test_indices, batch_size)
                total_loss, total_acc = 0, 0
                for ix in range(0, test_count, batch_size):                
                    batch_x, batch_features = get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
                    batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))                      
                    loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, y:batch_y, keep_prob:1.0})                
                    total_loss+= loss*batch_size
                    total_acc += acc*batch_size                
                print('  Test loss: {:.4f}, acc: {:.4f}'.format(total_loss/test_count, total_acc/test_count))

        print('!!!!!!!!!!!!!!! Optimization Finished !!!!!!!!!!!!!!!!!')

        if model_path_dest:
            saver = tf.train.Saver()            
            mfileu.create_directory(model_path_dest)
            saver.save(sess, model_path_dest+'/model')    
            print("Model Saved!")
        sess.close()
        
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
            batch_x, batch_features = get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
            batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
            pred = sess.run([prediction], feed_dict={x: batch_x, y:batch_y, keep_prob:1.0})            
            res[ix:ix+batch_size, 0] = np.array(pred).reshape((-1, ))
        
        res = res[:test_count_original, :]        
        sess.close()
        return res
        


# In[7]:


print("=============================  Train/Test: {} =============================".format(train_test))
print("\n============ Subject, Epochs, Win Size: {}, {}, {} =============".format(subj, num_epochs, win_size))


# In[8]:


path = mfileu.get_path()
model_folder_src = path+'/bite_models/our'
model_folder_dest = path+'/bite_models/our'
result_folder = 'our_test_proba_bite'


# In[9]:


def get_all_indices_lab(ssilv, exclude_subj, step=8):    
    ssil_all = []
    subj_count = len(ssilv)
    
    for subj in range(subj_count):        
        if subj==exclude_subj:
            continue
        for sess in range(len(ssilv[subj])):                         
            ssil = ssilv[subj][sess][::step, :4]
            ssil_all = ssil if len(ssil_all)==0 else np.concatenate((ssil_all, ssil))
    
    ssil_all = ssil_all.astype(int)
    cond = (ssil_all[:,-1]>=2) | (ssil_all[:,-1]<0 ) 
    ssil_all[cond, -1] = 0
    return ssil_all


# In[10]:


if train_test == 'train':
    print("Training.....")
    
    dlab = mfileu.read_file('data', 'lab_data_steven_right_smoothed.pkl')
    ssilv = mfileu.read_file('ssilv', 'lab_ssilv_steven_right.pkl')
    
    indices = get_all_indices_lab(ssilv, exclude_subj=subj)
    
    assert np.sum(indices[:, 0]==subj) == 0    
    assert np.sum(indices[:, -1]>1) == 0
    assert np.sum(indices[:, -1]<0) == 0
    print("Indices summary after subject filter total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
        
    indices = shuffle(indices)    
    train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    model_path_dest = model_folder_dest+"/subj_"+str(subj)
    train_test_model(dlab, train_indices, val_indices, params, model_path_dest=model_path_dest)  


# In[11]:


#train_test = 'test'
if train_test=='test_free':
    print("Testing Free.....")
    
    dfree = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
    ssilv_free = mfileu.read_file('ssilv', 'free_ssilv_steven_right.pkl')
    ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
        
    for subj in range(11):        
        for sess in range(len(dfree[subj])):
            ssil = ssilv_free[subj][sess][:, :4]
            indices = ssil.astype(int)
            print("\n\nSubj, sess, indices shape: ", subj, sess, indices.shape)
            print("Indices summary total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
        
            
            src_subj = subj+2 if subj<5 else 100            
            model_path_src = model_folder_src+"/subj_"+str(src_subj)            
            proba = train_test_model(dfree, [], indices, params, model_path_src=model_path_src)            
            print("Prediction shape: ", proba.shape, np.sum(proba), np.sum(proba>=0.5))            
            assert len(proba)==len(indices)
            
            gt = np.copy(ssilv_free[subj][sess][:, 3])
            ssilv_free[subj][sess][:, 3] = proba.reshape((-1, ))                    
            ba[subj][sess] = ssilv_free[subj][sess][:, 2:]
            print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1)/40)
            
    mfileu.write_file('all_proba', 'all_proba_bite_free_our.pkl', ba)         
    


# In[12]:


#train_test = 'test'
if train_test=='test_lab':
    print("Testing Lab .....")
    
    dlab = mfileu.read_file('data', 'lab_data_steven_right_smoothed.pkl')
    ssilv_lab = mfileu.read_file('ssilv', 'lab_ssilv_steven_right.pkl')
    ba = mfileu.read_file('data', 'lab_data_steven_blank_array.pkl')
        
    for subj in range(len(dlab)):        
        for sess in range(len(dlab[subj])):
            ssil = ssilv_lab[subj][sess][:, :4]
            indices = ssil.astype(int)
            print("\n\nSubj, sess, indices shape: ", subj, sess, indices.shape)
            print("Indices summary total, neg, pos:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
        
            src_subj = subj
            model_path_src = model_folder_src+"/subj_"+str(src_subj)            
            proba = train_test_model(dlab, [], indices, params, model_path_src=model_path_src)            
            print("Prediction shape: ", proba.shape, np.sum(proba), np.sum(proba>=0.5))            
            assert len(proba)==len(indices)
            
            gt = np.copy(ssilv_lab[subj][sess][:, 3])
            ssilv_lab[subj][sess][:, 3] = proba.reshape((-1, ))                    
            ba[subj][sess] = ssilv_lab[subj][sess][:, 2:]
            print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1)/40)
            
    mfileu.write_file('all_proba', 'all_proba_bite_lab_our.pkl', ba)         
    

