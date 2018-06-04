
# coding: utf-8

# In[ ]:


import numpy as np
import pickle
import os
import sys
import importlib
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split
import tensorflow as tf


# In[ ]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_steven_lab_utils as mslabu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
#importlib.reload(biteu)


# In[ ]:


axis_count, label_shape_1 = 6, 1 
win_size, step, feature_count = 5*16, 4, 32
var_min, var_max, gx_th = 1, 25, -0.25
print("Win size:", win_size, ", Step size:", step, ", var min:", var_min, ", var max:", var_max, ', gx_th:', gx_th)


# In[ ]:


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


# In[ ]:


subj, num_epochs, train_test = 0, 1, 'train'
if 'C:' not in mfileu.get_path():    
    subj, num_epochs, train_test = int(sys.argv[1]), int(sys.argv[2]), sys.argv[3]
    
assert train_test in ['train', 'test_lab', 'test_free']
params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.5


# In[ ]:


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
        


# In[ ]:


print("=============================  Train/Test: {} =============================".format(train_test))
print("\n============ Subject, Epochs, Win Size: {}, {}, {} =============".format(subj, num_epochs, win_size))


# In[ ]:


path = mfileu.get_path()
model_folder_src = path+'/bite_models/our_step'+str(step)
model_folder_dest = model_folder_src
result_folder = 'ipvg/ipvg_step'+str(step)


# In[ ]:


def get_train_ssilvg(ssilvg, exclude_subj):    
    res = []
    subj_count = len(ssilvg)    
    for subj in range(subj_count):        
        if subj==exclude_subj:
            continue
        for sess in range(len(ssilvg[subj])):                                     
            res = ssilvg[subj][sess] if len(res)==0 else np.concatenate((res, ssilvg[subj][sess]))
            
    return res


# In[ ]:


if train_test == 'train':
    print("Training.....")
    
    dlab = mfileu.read_file('data', 'lab_data_steven_right_smoothed.pkl')
    ssilvg = mfileu.read_file('ssilvg/ssilvg_step'+str(step), 'lab_ssilvg_steven_right.pkl')
    
    ix = get_train_ssilvg(ssilvg, exclude_subj=subj)    
    print("Indices summary before filter total, neg, pos:", ix.shape, np.sum(ix[:,3]==0), np.sum(ix[:,3]==1))    
    
    l, v, g  = ix[:, 3], ix[:, 4], ix[:, 5]
    cond1, cond2, cond3, cond4  = (v>=1), (v<=50), (g<0), (l>=0)
    cond = cond1 & cond2 & cond3 & cond4    
    c = len(ix)
    print("Cond filter v: {}, {}, g:{}, x:{}, Total filtered: {}".format(c-np.sum(cond1), c-np.sum(cond2), c-np.sum(cond3), c-np.sum(cond4), c-np.sum(cond)))
    
    cond_pos = (ix[:,3]==1)
    print("pos_count: ", np.sum(cond_pos))
    print("Pos counts V: {}, {}, g: {}, x:{}, Remain pos: {}".format(np.sum(cond_pos&cond1), np.sum(cond_pos& cond2), np.sum(cond_pos&cond3), np.sum(cond_pos&cond4), c-np.sum(cond_pos&cond)))
    
    ix = ix[cond, :4].astype(int)    
    cond = (ix[:,-1]>=2) 
    ix[cond, -1] = 0 #set sip labels to 0
    print("Indices summary after filter total, neg, pos:", len(ix), np.sum(ix[:, -1]==0), np.sum(ix[:, -1]==1))
    
    assert np.sum(ix[:, 0]==subj) == 0    
    assert np.sum(ix[:, -1]>1) == 0
    assert np.sum(ix[:, -1]<0) == 0    
        
    ix = shuffle(ix)    
    train_indices, val_indices = train_test_split(ix, test_size=0.1, stratify=ix[:, -1])
    
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    model_path_dest = model_folder_dest+"/subj_"+str(subj)
    train_test_model(dlab, train_indices, val_indices, params, model_path_dest=model_path_dest)  


# In[ ]:


#train_test = 'test'
if train_test=='test_lab' or train_test=='test_free':    
    lab_free = 'lab' if train_test=='test_lab' else 'free'    
    print("Testing {} .....".format(lab_free))
    
    ds = mfileu.read_file('data', '{}_data_steven_right_smoothed.pkl'.format(lab_free))
    ssilvg = mfileu.read_file('ssilvg/ssilvg_step'+str(step), '{}_ssilvg_steven_right.pkl'.format(lab_free))
    ba = mfileu.read_file('data', '{}_data_steven_blank_array.pkl'.format(lab_free))
        
    for subj in range(len(ds)):        
        for sess in range(len(ds[subj])):
            
            ix = ssilvg[subj][sess][:, :4].astype(int)            
            print("\n\nSubj, sess, indices shape: ", subj, sess, ix.shape)
            print("Indices summary total, neg, pos:", len(ix), np.sum(ix[:, -1]==0), np.sum(ix[:, -1]==1))
        
            if lab_free=='lab':
                src_subj = subj
            else:
                src_subj = subj+2 if subj<5 else 100            
            
            model_path_src = model_folder_src+"/subj_"+str(src_subj)            
            proba = train_test_model(ds, [], ix, params, model_path_src=model_path_src)            
            print("Prediction shape: ", proba.shape, np.sum(proba), np.sum(proba>=0.5))            
            assert len(proba)==len(ix)
            
            v, g  = ssilvg[subj][sess][:, 4], ssilvg[subj][sess][:, 5]
            cond1, cond2, cond3  = (v>=1), (v<=50), (g<0)
            cond = np.logical_not(cond1 & cond2 & cond3)               
            proba[cond] = 0            
            
            gt = np.copy(ssilvg[subj][sess][:, 3])
            ssilvg[subj][sess][:, 3] = proba.reshape((-1, ))                    
            ba[subj][sess] = ssilvg[subj][sess][:, 2:]
            print("Proba shape, pos, gtpos:", proba.shape, np.sum(proba>=0.5), np.sum(gt==1)/40)
            
    mfileu.write_file('ipvg/ipvg_step'+str(step), '{}_ipvg_our.pkl'.format(lab_free), ba)         
    

