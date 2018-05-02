
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
import Meal_Window_Generation_Utils as mwgenu


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
import my_feature_utils as mfeatu

importlib.reload(mwgenu)
#importlib.reload(mfeatu)
#importlib.reload(mcnnu)
#importlib.reload(mlstmu)
#importlib.reload(mdenseu)
#importlib.reload(mclfu)


# In[3]:


win_size, neg_step, pos_step, test_step  = 10*16, 8, 8, 8
vth_min, vth_max, xth = 1, 50, 0
axis_count = 9
label_shape_1 = 1
folder_suffix = "_meal_free_winsize_"+str(win_size)+"_vth_"+str(vth_min)+"_"+str(vth_max)+"_xth_"+str(xth)


# In[40]:


hand='left'
ds = mfileu.read_file('data', 'free_data_steven_'+hand+'_smoothed.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[45]:


subj, num_epochs = 1, 1
train = 0
if 'C:' not in mfileu.get_path():    
    subj, num_epochs = int(sys.argv[1]), int(sys.argv[2])

params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.5


# In[42]:


def train_test_model(train_indices, test_indices, params, model_path=None):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']
    
    train_result, test_result = [], []
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, win_size, axis_count], name="x")
    y = tf.placeholder(tf.float32, [None, label_shape_1], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    
    cnn_out = mcnnu.all_sensor_net(x, name="all_sensor_net")
    lstm_out_fw, lstm_out_bw = mlstmu.multi_layer_biLSTM(cnn_out, batch_size=batch_size, n_hidden=100, n_layer=2)
    print("Lstm out shapes(fw, bw): ", lstm_out_fw.get_shape().as_list(), lstm_out_bw.get_shape().as_list())
    print("FW LSTM out shape:", lstm_out_fw[:, -1, :].get_shape().as_list())
    lstm_out = tf.concat([lstm_out_fw[:, -1, :], lstm_out_bw[:, 0, :]], axis =1)
    print("Lstm out shape final: ", lstm_out.get_shape().as_list())
    
    drop_layer = tf.nn.dropout(lstm_out, keep_prob=keep_prob, name="dropout")
    print("Drop layer shape: ",drop_layer.get_shape().as_list())
    logits = mdenseu.fc_layer(drop_layer, 1, name="Logits")    
    
    print("Logit shape: ",logits.get_shape().as_list())
    prediction = tf.nn.sigmoid(logits, name="prediction")
    correct_prediction = tf.equal(tf.greater(prediction, 0.5), tf.equal(y,1), name="correct_prediction")
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")
    
    loss_op = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")    
    train_step = tf.train.AdamOptimizer(learning_rate).minimize(loss_op, name="train_step")

    sess = tf.Session()
    
    ########## Train and then save the model ########################
    if len(train_indices)>0:    
        sess.run(tf.global_variables_initializer())    
        train_indices, _ = mclfu.adjust_for_batch_size(train_indices, train_indices, batch_size)

        train_count = len(train_indices)
        for epoch in range(num_epochs):
            print("Epoch:", epoch)
            total_loss, total_acc = 0, 0
            for ix in range(0, train_count, batch_size):                                            
                batch_x = mwgenu.get_window_data(ds, train_indices[ix:ix+batch_size], win_size)
                batch_y = train_indices[ix:ix+batch_size, -1].reshape((-1,1))                 
                sess.run(train_step, feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})            

                loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})        
                total_loss+= loss
                total_acc += acc
                #if ix%(batch_size*1000)==0:
                #    print("{}/{} :: loss: {:.4f}, acc:{:.4f}".format(ix, train_count, loss, acc ), end="; ")
            print('  Train loss: {:.4f}, acc: {:.4f}'.format(batch_size*total_loss/train_count, batch_size*total_acc/train_count))

            test_count = len(test_indices)
            if test_count>0:            
                test_indices, _ = mclfu.adjust_for_batch_size(test_indices, test_indices, batch_size)
                total_loss, total_acc = 0, 0
                for ix in range(0, test_count, batch_size):                
                    batch_x = mwgenu.get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
                    batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
                    
                    loss, acc = sess.run([loss_op, accuracy], feed_dict={x: batch_x, y: batch_y, keep_prob:1.0})                
                    total_loss+= loss
                    total_acc += acc                
                print('  Test loss: {:.4f}, acc: {:.4f}'.format(batch_size*total_loss/test_count, batch_size*total_acc/test_count))

        print('!!!!!!!!!!!!!!! Optimization Finished !!!!!!!!!!!!!!!!!')

        if model_path:
            saver = tf.train.Saver()            
            mfileu.create_directory(model_path)
            saver.save(sess, model_path+'/model')    
            print("Model Saved!")
        sess.close()
        
    ########## Restore the model and then Test  ########################
    else:
        saver = tf.train.Saver()
        saver.restore(sess, model_path+'/model')
        print("Model Loaded!")
        
        test_count_original = len(test_indices)        
        test_indices, _ = mclfu.adjust_for_batch_size(test_indices, test_indices, batch_size)
        test_count = len(test_indices)
        res = np.zeros((test_count, 1))
        
        for ix in range(0, test_count, batch_size):                
            batch_x = mwgenu.get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
            batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
            pred = sess.run([prediction], feed_dict={x: batch_x, y: batch_y, keep_prob:1.0})            
            res[ix:ix+batch_size, 0] = np.array(pred).reshape((-1, ))
        
        res = res[:test_count_original, :]        
        sess.close()
        return res
        


# In[46]:


print("\n============ Subject, Epochs, Hand, Win Size: {}, {}, {}, {} =============".format(subj, num_epochs, hand, win_size))

if train:
    print("Training.....")
    indices = mwgenu.get_train_window_indices_all(ds, annots, win_size=win_size, neg_step=neg_step, pos_step=pos_step, exclude_subject=subj)    
    indices = indices[(indices[:, -1]<=1), :]
    assert np.sum(indices[:, -1]>1) == 0
    print("Indices shape before filter, pos_count: ", indices.shape, np.sum(indices[:, -1]))
    
    v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
    gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)
    if hand=='right':
        indices = indices[(v>=vth_min)&(v<=vth_max)&(gx<=xth), :]
    else:
        indices = indices[(v>=vth_min)&(v<=vth_max)&(gx>=xth), :]
    print("Indices shape after filter, pos_count ", indices.shape, np.sum(indices[:, -1]))
    
    indices = shuffle(indices)
    train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    path = mfileu.get_path()
    train_test_model(train_indices, val_indices, params, path+"/models"+ folder_suffix+"/subj_"+str(subj)+"_"+hand)
    
else:
    print("Testing.....")
    path = mfileu.get_path()
    
    #Test on Free Data
    for subj in range(11):
        res=[]
        print("---------------- Testing Free -----------------------------")
        for sess in range(len(ds[subj])):
            indices = mwgenu.get_test_window_indices(ds, annots, win_size, step_size=test_step, subj=subj, sess=sess)        
            print("Free Subj, sess, indices shape: ", subj, sess, indices.shape)
            
            v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
            gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)
            
            pred = train_test_model([], indices, params, path+"/models"+ folder_suffix+"/subj_"+str(subj)+"_"+hand)
            print("Prediction shape: ", pred.shape)        

            res.append({"pred":pred, 'indices':indices, "var":v, "gx":gx})
            print ("Test done for subject, session :", subj, sess)
            #gt = indices[:, -1].reshape((-1, 1))
            #print(mclfu.get_scores_1d(pred, gt))
            print("\n----------------------------------\n")

        mfileu.write_file('results'+ folder_suffix+"_free", 'subj_'+str(subj)+"_"+hand+".pkl", res)
        
        
    #Test on Lab Data
    ds = mfileu.read_file('data', 'lab_data_steven_smoothed.pkl')
    ds_right, ds_left, annots = mslabu.separate_right_left_annots(dsa)    
    ds = ds_right if hand=='right' else ds_left
    for subj in range(7):
        res=[]
        print("----------------- Testing Lab ----------------------------")
        for sess in range(len(ds[subj])):
            indices = mwgenu.get_test_window_indices(ds, [], win_size, step_size=test_step, subj=subj, sess=sess)        
            print("Lab subj, sess, indices shape: ", subj, sess, indices.shape)
            
            v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
            gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)
            
            model_subject = subj-2 if subj>=2 else 10 
            pred = train_test_model([], indices, params, path+"/models"+ folder_suffix+"/subj_"+str(model_subject)+"_"+hand)
            print("Prediction shape: ", pred.shape)        

            res.append({"pred":pred, 'indices':indices, "var":v, "gx":gx})
            print ("Test done for subject, session :", subj, sess)            
            print("\n----------------------------------\n")

        mfileu.write_file('results'+ folder_suffix+"_lab", 'subj_'+str(subj)+"_"+hand+".pkl", res)

