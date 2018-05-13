
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


# In[3]:


step_size = 8
vth_min, vth_max, xth = 1, 30, 0
axis_count, label_shape_1 = 9, 1 


# In[4]:


hand='right'
ds = mfileu.read_file('data', 'free_data_steven_'+hand+'_smoothed.pkl')
annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[10]:


subj, num_epochs, win_duration, train_code = 1, 1, 10, 4
if 'C:' not in mfileu.get_path():    
    subj, num_epochs, win_duration, train_code = int(sys.argv[1]), int(sys.argv[2]), int(sys.argv[3]), int(sys.argv[4])

print("\n============ Subject:{}, Epochs:{}, Win Duration:{}, Train_code:{} =============".format(subj, num_epochs, win_duration, train_code))

assert train_code in [1, 2, 3, 4]
assert 0<=subj<=11
assert 0<num_epochs<=100
assert win_duration>0
    
win_size = win_duration*16
folder_suffix = "win_"+str(win_duration)+"_vth_"+str(vth_min)+"_"+str(vth_max)+"_xth_"+str(xth)
print(folder_suffix)
    
learning_rate = 0.001
num_epochs = num_epochs
batch_size = 128
keep_prob_val = 0.5


# In[6]:


def train_test_model(train_indices, test_indices, model_path_src=None, model_path_dest=None):   
    
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
    
    #load model for test or retrain
    if train_code >=2:
        saver = tf.train.Saver()
        saver.restore(sess, model_path_src+'/model')
        print("************************ Model Loaded! ************************")            
    
    ########## Train and then save the model ########################
    if train_code<=2:   # train of retrain        
        if train_code==1:
            print("============== Training =====================")
            sess.run(tf.global_variables_initializer())
        else:
            print("============== Re-Training =====================")
            
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

        
        saver = tf.train.Saver()        
        mfileu.create_directory(model_path_dest)
        saver.save(sess, model_path_dest+'/model')    
        print("Model Saved!")
        
        sess.close()
        
    ########## Restore the model and then Test  ########################
    else: #test
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
        


# In[11]:


path = mfileu.get_path()

if train_code==1:
    print("Training.....")
    indices = mwgenu.get_window_indices_all(ds, annots, win_size=win_size, step_size=step_size, subject=subj, exclude=True)    
    indices = indices[(indices[:, -1]<=1), :]
    assert np.sum(indices[:, -1]>1) == 0
    print("Indices shape before filter, pos_count: ", indices.shape, np.sum(indices[:, -1]))
    
    v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
    gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)    
    indices = indices[(v>=vth_min)&(v<=vth_max)&(gx<=xth), :]
    
    print("Indices shape after filter, pos_count ", indices.shape, np.sum(indices[:, -1]))
    
    indices = shuffle(indices)
    train_indices, val_indices = train_test_split(indices, test_size=0.1, stratify=indices[:, -1])
    print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
    
    model_path_dest = path+"/models_free/"+ folder_suffix+"/subj_"+str(subj)+"_"+hand
    train_test_model(train_indices, val_indices, model_path_dest = model_path_dest)

    
elif train_code==2:
    print("Re-Training.....")
    indices = mwgenu.get_window_indices_all(ds, annots, win_size=win_size, step_size=step_size, subject=subj, exclude=False)    
    indices = indices[(indices[:, -1]<=1), :]
    assert np.sum(indices[:, -1]>1) == 0
    print("Indices shape before filter, pos_count: ", indices.shape, np.sum(indices[:, -1]))
    
    v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
    gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)    
    indices = indices[(v>=vth_min)&(v<=vth_max)&(gx<=xth), :]
    
    ("Indices shape after filter, pos_count ", indices.shape, np.sum(indices[:, -1]))
    
    
    for sess in range(len(ds[subj])):
        print("========= Retraing Subject, Session: {}, {} ==========".format(subj, sess))
        sess_indices = indices[indices[:, 1]!=sess, :]
        sess_indices = shuffle(sess_indices)
        train_indices, val_indices = train_test_split(sess_indices, test_size=0.1, stratify=sess_indices[:, -1])
        print("train, val shapes: ", train_indices.shape, val_indices.shape, np.sum(train_indices[:, -1]), np.sum(val_indices[:, -1]))
        
        model_path_src = path+"/models_free/"+ folder_suffix+"/subj_"+str(subj)+"_"+hand
        model_path_dest = path+"/models_free_personal/"+ folder_suffix+"/subj_"+str(subj)+"_sess_"+str(sess)+"_"+hand
        train_test_model(train_indices, val_indices, model_path_src=model_path_src, model_path_dest=model_path_dest)
    
    
elif train_code==3:
    print("Testing Lopo.....")    
    for subj in range(11):        
        print("---------------- Testing Free LOPO-----------------------------")

        indices = mwgenu.get_window_indices_all(ds, annots, win_size=win_size, step_size=step_size, subject=subj, exclude=False)
        print("Free Subj, indices shape: ", subj, indices.shape)

        v = mfeatu.get_variance_accel(ds, indices, win_size=win_size)
        gx = mfeatu.get_grav_x(ds, indices, index_offset=win_size//2)

        model_path_src = path+"/models_free/"+ folder_suffix+"/subj_"+str(subj)+"_"+hand
        pred = train_test_model([], indices, model_path_src=model_path_src)
        print("Prediction shape: ", pred.shape)        

        res = {"pred":pred, 'indices':indices, "var":v, "gx":gx}
        print ("Test done for subject :", subj)        
        print("\n----------------------------------\n")        
        
        mfileu.write_file('/results_free/'+ folder_suffix, 'subj_'+str(subj)+"_"+hand+".pkl", res)
        
else:
    print("Testing Loso.....")    
    for subj in range(1,2):        
        print("---------------- Testing Free LOSO-----------------------------")
        indices = mwgenu.get_window_indices_all(ds, annots, win_size=win_size, step_size=step_size, subject=subj, exclude=False)
        print("Free Subj, indices shape: ", subj, indices.shape)

        for sess in range(len(ds[subj])):
            sess_indices = indices[indices[:, 1]==sess, :]

            v = mfeatu.get_variance_accel(ds, sess_indices, win_size=win_size)
            gx = mfeatu.get_grav_x(ds, sess_indices, index_offset=win_size//2)

            model_path_src = path+"/models_free_personal/"+ folder_suffix+"/subj_"+str(subj)+"_sess_"+str(sess)+"_"+hand
            pred = train_test_model([], sess_indices, model_path_src = model_path_src)
            print("Prediction shape: ", pred.shape)        

            res = {"pred":pred, 'indices':indices, "var":v, "gx":gx}
            print ("Re-Test done for subject :", subj)        
            print("\n----------------------------------\n")

            mfileu.write_file('/results_free_personal/'+ folder_suffix+"_free", 'subj_'+str(subj)+"_sess_"+str(sess)+"_"+hand+".pkl", res)

    

