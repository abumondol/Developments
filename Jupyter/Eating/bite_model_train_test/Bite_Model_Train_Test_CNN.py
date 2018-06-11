
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
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
#importlib.reload(biteu)


# In[ ]:


ds_lab = mfileu.read_file('data', 'lab_data_steven_right_smoothed_accel_gyro_normalized.pkl')
ssil_lab = mfileu.read_file('peaks', 'lab_ssil_steven_right.pkl')
ds_free = mfileu.read_file('data', 'free_data_steven_right_smoothed_accel_gyro_normalized.pkl')
ssil_free = mfileu.read_file('peaks', 'free_ssil_steven_right.pkl')


# In[ ]:


ds = [ds_lab, ds_free]

count = len(ssil_lab)
ds_index = np.zeros((count, 1), dtype=np.int32)
dssil_lab =np.concatenate((ds_index, ssil_lab), axis=1).astype(int)
print("Lab ssil, dssil: ", ssil_lab.shape, dssil_lab.shape, np.sum(dssil_lab[:,0]))
l = dssil_lab[:, -1]
print("Lab label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))
print()

count = len(ssil_free)
ds_index = np.ones((count, 1), dtype=np.int32)
dssil_free =np.concatenate((ds_index, ssil_free), axis=1).astype(int)
print("Free ssil, dssil: ", ssil_free.shape, dssil_free.shape, np.sum(dssil_free[:,0]))
l = dssil_free[:, -1]
print("Free label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))


# In[ ]:


axis_count, label_shape_1 = 6, 1 
win_size = 10*16
print("Win size:", win_size)


# In[ ]:


def get_window_data(ds, dssil, win_size):
    count = len(dssil)
    w = np.zeros((count, win_size, axis_count))
    features = []
    
    half_win_size = win_size//2
    for i in range(count):
        d, subj, sess, ix = dssil[i, 0], dssil[i, 1], dssil[i, 2], dssil[i, 3]
        w[i, :, :] = ds[d][subj][sess][ix-half_win_size:ix+half_win_size, 1:7]        
    
    return w, features


# In[ ]:


subj, num_epochs, train_test = 0, 50, 'train'
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
    
    #print_out, sys.stdout = sys.stdout, open(os.devnull, 'w')    
    
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, win_size, axis_count], name="x")    
    #features = tf.placeholder(tf.float32, [None, feature_count], name="features")
    y = tf.placeholder(tf.float32, [None, label_shape_1], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    cnn_out = mcnnu.all_sensor_net(x, name="all_sensor_net")    
    lstm_out_fw, lstm_out_bw = mlstmu.multi_layer_biLSTM(cnn_out, batch_size=batch_size, n_hidden=128, n_layer=1)
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
    
    #sys.stdout = print_out
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
model_folder_src = path+'/free_models_our/net_1'
model_folder_dest = model_folder_src


# In[ ]:


def get_train_dssil(subj, dssil_lab, dssil_free):
    
    ########### Lab ###########
    if subj<5:
        cond = (dssil_lab[:,1]!=subj+2)
        dssil_lab = dssil_lab[cond, :]
    elif subj>=200:
        cond = (dssil_lab[:,1]!=subj-200)
        dssil_lab = dssil_lab[cond, :]
    
    print("Lab dssil: ", dssil_lab.shape, np.sum(dssil_lab[:, 0]))
    l = dssil_lab[:, -1]
    print("Lab label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))
    dssil_lab = dssil_lab[(l==1) |(l==2), :]
    dssil_lab[:, -1] = 1    
    l = dssil_lab[:, -1]
    print("Lab label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))
    
    ############ Free #############
    if subj<100:
        cond = (dssil_free[:,1]!=subj)
        dssil_free = dssil_free[cond, :]
    
    print("\nFree dssil: ", dssil_free.shape, np.sum(dssil_free[:,0]))
    l = dssil_free[:, -1]
    print("Free label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))
    #dssil_free[l==2, -1] = 1
    #dssil_free[l==3, -1] = 0    
    l = dssil_free[:, -1]
    dssil_free = dssil_free[(l==0), :] #taking only negatives from free
    l = dssil_free[:, -1]
    print("Free label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))

    ############ Combo #############
    dssil = np.concatenate((dssil_lab, dssil_free))
    print("\nCombo dssil: ", dssil.shape, np.sum(dssil[:,0]==0), np.sum(dssil[:,0]==1))    
    l = dssil[:, -1]
    print("Combo label summary:",  np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))
    
    return dssil
    


# In[ ]:


if train_test == 'train':
    print("Training.....")
    
    dssil = get_train_dssil(subj, dssil_lab, dssil_free)
    
    assert np.sum(dssil[:, -1]>1) == 0
    assert np.sum(dssil[:, -1]<0) == 0    
        
    dssil = shuffle(dssil)    
    train_indices, val_indices = train_test_split(dssil, test_size=0.1, stratify=dssil[:, -1])
    
    print("\nTrain, Val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    model_path_dest = model_folder_dest+"/subj_"+str(subj)
    train_test_model(ds, train_indices, val_indices, params, model_path_dest=model_path_dest)  


# In[ ]:


#train_test = 'test'
if train_test=='test_free':       
    print("Testing Free .....")    
    ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
    
    print(ssil_free.shape, dssil_free.shape)
    for subj in range(len(ds_free)):
        cond = (dssil_free[:, 1]==subj)
        dssil = dssil_free[cond, :]
        assert np.sum(dssil[:, 1]!=subj) == 0

        l = dssil[:, -1]
        print("\n\nSubj, Label summary:", subj, dssil.shape, np.sum(l==0), np.sum(l==1), np.sum(l==2), np.sum(l==3))

        model_path_src = model_folder_src+"/subj_"+str(subj)            
        proba = train_test_model(ds, [], dssil, params, model_path_src=model_path_src)            
        print("\n\nPrediction shape, sum, >=0.5: ", proba.shape, np.sum(proba), np.sum(proba>=0.5))            
        assert len(proba)==len(dssil)
        
        for sess in range(len(ds_free[subj])):
            cond = (dssil[:, 2]==sess)
            ssil = dssil[cond, 1:]
            p = proba[cond, :]
            assert np.sum(ssil[:, 0]!=subj) == 0
            assert np.sum(ssil[:, 1]!=sess) == 0
            
            print("Subj, sess: ", subj, sess, len(ssil))
            
            ba[subj][sess] = np.concatenate((ssil, p), axis=1)
            print("ssilp shape: ", ba[subj][sess].shape)

    mfileu.write_file('peak_ssilp', 'free_ssilp_our_net_1.pkl', ba)

