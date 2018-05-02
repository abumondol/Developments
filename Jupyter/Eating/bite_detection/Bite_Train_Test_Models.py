
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
import Bite_Utils as biteu


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_classification_utils as mclfu
import my_tensorflow_cnn_utils as mcnnu
import my_tensorflow_lstm_utils as mlstmu
import my_tensorflow_dense_utils as mdenseu
import my_feature_utils as mfeatu
import my_steven_lab_utils as mslabu
importlib.reload(biteu)


# In[3]:


axis_count = 9
label_shape_1 = 1


# In[4]:


hand = 'right'
win_size, feature_count = 3*16, 93
varth, xth = 0.5, -0.3
min_bite_interval, min_stay, boundary_offset = 2*16, 8, 2*win_size
label_max_distance, label_buffer_distance = 2*16, 4*16

folder_suffix = '_bite_detection'


# In[5]:


dsa = mfileu.read_file('data', 'lab_data_steven.pkl')
ds_right, ds_left, annots = mslabu.separate_right_left_annots(dsa)
annots = mslabu.adjust_annots_all(annots)
ds = ds_right  if hand=='right' else ds_left    
annots = mslabu.filter_annots_all(annots, hand=hand)


# In[6]:


subj, num_epochs, train = 100, 50, 0
if 'C:' not in mfileu.get_path():    
    subj, num_epochs, train = int(sys.argv[1]), int(sys.argv[2]), int(sys.argv[3])

params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.5


# In[7]:


pos_weight_val = 0
def train_test_model(train_indices, test_indices, params, model_path=None):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']    
    
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, win_size, axis_count], name="x")
    xrev = tf.placeholder(tf.float32, [None, win_size, axis_count], name="xrev")
    features = tf.placeholder(tf.float32, [None, feature_count], name="features")
    y = tf.placeholder(tf.float32, [None, label_shape_1], name="y")
    keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    pos_weight = tf.placeholder(tf.float32, name="pos_weight")    
    
    
    cnn_out = mcnnu.all_sensor_net_bite(x, name="all_sensor_net")
    lstm_out = mlstmu.multi_layer_LSTM(cnn_out, batch_size=batch_size, n_hidden=32, n_layer=1, name_scope='lstm_left_window')
    
    cnn_out_rev = mcnnu.all_sensor_net_bite(xrev, name="all_sensor_net_rev")
    lstm_out_rev = mlstmu.multi_layer_LSTM(cnn_out_rev, batch_size=batch_size, n_hidden=32, n_layer=1, name_scope='lstm_right_window')
    
    print("Lstm out shapes(fw, bw): ", lstm_out.get_shape().as_list(), lstm_out_rev.get_shape().as_list())
    print("LSTM last output shape:", lstm_out[:, -1, :].get_shape().as_list())
    
    lstm_out = tf.concat([lstm_out[:, -1, :], lstm_out_rev[:, -1, :]], axis =1)
    #lstm_out = tf.concat([lstm_out[:, -1, :], lstm_out_rev[:, -1, :], features], axis =1)    
    print("Lstm out shape combined: ", lstm_out.get_shape().as_list())
    #lstm_feature_combo = tf.concat([lstm_out, features], axis =1)
    #print("Lstm feature shape combined: ", lstm_feature_combo.get_shape().as_list())
    
    #fc_layer= mdenseu.fc_layer(lstm_out, 64, name="Dense_1", activation='relu')    
    drop_layer = tf.nn.dropout(lstm_out, keep_prob=keep_prob, name="dropout")
    print("Drop layer shape: ",drop_layer.get_shape().as_list())
    logits = mdenseu.fc_layer(drop_layer, 1, name="Logits")    
    
    print("Logit shape: ",logits.get_shape().as_list())
    prediction = tf.nn.sigmoid(logits, name="prediction")
    correct_prediction = tf.equal(tf.greater(prediction, 0.5), tf.equal(y,1), name="correct_prediction")
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")
    
    if pos_weight_val==0:
        loss_op = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")    
    else:
        loss_op = tf.reduce_mean(tf.nn.weighted_cross_entropy_with_logits(logits=logits, targets=y, pos_weight=pos_weight), name="loss_op")    
        
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
                batch_x, batch_xrev, batch_features = biteu.get_window_data(ds, train_indices[ix:ix+batch_size], win_size)                 
                batch_y = train_indices[ix:ix+batch_size, -1].reshape((-1,1))                 
                sess.run(train_step, feed_dict={x:batch_x, xrev:batch_xrev, features:batch_features, y:batch_y, keep_prob:keep_prob_val})            

                loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, xrev:batch_xrev, features:batch_features, y:batch_y, pos_weight:pos_weight_val, keep_prob:keep_prob_val})        
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
                    batch_x, batch_xrev, batch_features = biteu.get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
                    batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
                    
                    loss, acc = sess.run([loss_op, accuracy], feed_dict={x:batch_x, xrev:batch_xrev, features:batch_features, y:batch_y, pos_weight:pos_weight_val, keep_prob:1.0})                
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
            batch_x, batch_xrev, batch_features = biteu.get_window_data(ds, test_indices[ix:ix+batch_size], win_size)
            batch_y = test_indices[ix:ix+batch_size, -1].reshape((-1,1))  
            pred = sess.run([prediction], feed_dict={x: batch_x, xrev:batch_xrev, features:batch_features, y:batch_y, pos_weight:pos_weight_val, y: batch_y, keep_prob:1.0})            
            res[ix:ix+batch_size, 0] = np.array(pred).reshape((-1, ))
        
        res = res[:test_count_original, :]        
        sess.close()
        return res
        


# In[ ]:


print("=============================  Train/Test: {} =============================".format(train))
print("\n============ Subject, Epochs, Hand, Win Size: {}, {}, {}, {} =============".format(subj, num_epochs, hand, win_size))

if train>=1:
    print("Training.....")
    mps, acs = biteu.get_min_points_all(ds, annots,
                                    xth = xth, 
                                    min_bite_interval=min_bite_interval,
                                    min_stay = min_stay,
                                    boundary_offset = boundary_offset,
                                    label_max_distance=label_max_distance,
                                    label_buffer_distance = label_buffer_distance,
                                    block_print = True)
    biteu.print_label_summary(mps, acs)
    
    indices = mps[mps[:, 0]!=subj, :]
    assert np.sum(indices[:, 0]==subj) == 0    
    print("Indices summary after subject filter total, neg, bite:", len(indices), np.sum(indices[:, -2]==0), np.sum(indices[:, -2]==1))
    
    indices = indices[(indices[:, -2]>=0) & (indices[:, -2]<=1)&(indices[:, -1]==0), :4]
    assert np.sum(indices[:, -1]>1) == 0
    assert np.sum(indices[:, -1]<0) == 0    
    print("Indices summary after sip and ambigous filter total, neg, bite:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
    
    ix = indices[:, :3]
    ix[:, 2] = ix[:, 2]-win_size
    v = mfeatu.get_variance_accel(ds, ix, win_size=2*win_size)            
    indices = indices[v>=varth, :]
    print("Indices summary after variance filter total, neg, bite:", len(indices), np.sum(indices[:, -1]==0), np.sum(indices[:, -1]==1))
        
    indices = shuffle(indices)
    if train>1:
        train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    else:
        train_indices, val_indices = indices,np.zeros((0, 4))
        
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    path = mfileu.get_path()
    train_test_model(train_indices, val_indices, params, path+"/models"+ folder_suffix+"/subj_"+str(subj)+"_"+hand)
    
else:
    print("Testing.....")
    path = mfileu.get_path()
    
    
    ###################### Test Lab Data
    mps, acs = biteu.get_min_points_all(ds, annots, 
                                    xth = xth, 
                                    min_bite_interval=min_bite_interval,
                                    min_stay = min_stay,
                                    boundary_offset = boundary_offset,
                                    label_max_distance=label_max_distance,
                                    label_buffer_distance = label_buffer_distance,
                                    block_print = True)
    indices = mps
    print("MPS summary after total, x, neg, bite, sip:", len(indices), np.sum(indices[:, -2]<0), np.sum(indices[:, -2]==0), np.sum(indices[:, -2]==1), np.sum(indices[:, -2]==2))
        
    for subj in range(7):
        if subj>=0: continue
        res=[]
        print("---------------------------------------------")
        for sess in range(len(ds[subj])):
            indices = mps[(mps[:, 0]==subj) & (mps[:, 1]==sess), :4]
            print("Subj, sess, indices shape: ", subj, sess, indices.shape)
            print("Test Indices summary total, x, neg, bite, sip:", len(indices), np.sum(indices[:, -2]<0), np.sum(indices[:, -2]==0), np.sum(indices[:, -2]==1), np.sum(indices[:, -2]==2))
            
            ix = indices[:,:3]
            ix[:, 2] = ix[:, 2]-win_size
            v = mfeatu.get_variance_accel(ds, ix, win_size=2*win_size)            
            gx = mfeatu.get_grav_x(ds, indices)      
                  
            pred = train_test_model([], indices, params, path+"/models"+ folder_suffix+"/subj_"+str(subj)+"_"+hand)
            print("Prediction shape: ", pred.shape)
            print("Shapes pred, indices, v, gx: ", pred.shape, indices.shape, v.shape, gx.shape)        
            assert len(pred)==len(indices)
            
            res.append({"pred":pred, 'indices':indices, "var":v, "gx":gx})
            print ("Lab Test done for subject, session :", subj, sess)
            gt = indices[:, -1].reshape((-1, 1))
            print(mclfu.get_scores_1d(pred, gt))
            print("\n----------------------------------\n")

        mfileu.write_file('results'+ folder_suffix+"_lab", 'subj_'+str(subj)+"_"+hand+".pkl", res)
    
    ################# Test Free Data
    ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
    mps = biteu.get_min_points_all_free(ds, 
                                    xth = xth, 
                                    min_bite_interval=min_bite_interval,
                                    min_stay = min_stay,
                                    boundary_offset = boundary_offset,
                                    label_max_distance=label_max_distance,
                                    label_buffer_distance = label_buffer_distance,
                                    block_print = True)
    indices = mps
    print("MPS summary after total, x, neg, bite, sip:", len(indices), np.sum(indices[:, -2]<0), np.sum(indices[:, -2]==0), np.sum(indices[:, -2]==1), np.sum(indices[:, -2]==2))          
                  
    for subj in range(11):
        res=[]
        print("---------------------------------------------")
        for sess in range(len(ds[subj])):
            indices = mps[(mps[:, 0]==subj) & (mps[:, 1]==sess), :4]
            print("Subj, sess, indices shape: ", subj, sess, indices.shape)
            print("Test Indices summary total, x, neg, bite, sip:", len(indices), np.sum(indices[:, -2]<0), np.sum(indices[:, -2]==0), np.sum(indices[:, -2]==1), np.sum(indices[:, -2]==2))
            
            ix = indices[:, :3]
            ix[:, 2] = ix[:, 2]-win_size
            v = mfeatu.get_variance_accel(ds, ix, win_size=2*win_size) 
            gx = mfeatu.get_grav_x(ds, indices)      
                  
            model_subject = subj+2 if 0<=subj<=4 else 7                  
            pred = train_test_model([], indices, params, path+"/models"+ folder_suffix+"/subj_"+str(model_subject)+"_"+hand)
            print("Shapes pred, indices, v, gx: ", pred.shape, indices.shape, v.shape, gx.shape)        
            assert len(pred)==len(indices)
            
            
            res.append({"pred":pred, 'indices':indices, "var":v, "gx":gx})
            print ("Free Test done for subject, session :", subj, sess)
            gt = indices[:, -1].reshape((-1, 1))
            print(mclfu.get_scores_1d(pred, gt))
            print("\n----------------------------------\n")

        mfileu.write_file('results'+ folder_suffix+"_free", 'subj_'+str(subj)+"_"+hand+".pkl", res)

