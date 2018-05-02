
# coding: utf-8

# In[17]:


import numpy as np
import pickle
import os
import sys
import importlib
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split

import tensorflow as tf


# In[18]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu
import my_tensorflow_cnn_utils as mcnnu
import my_classification_utils as mclfu
importlib.reload(mcnnu)


# In[21]:


def train_test_model(train_x, train_y, test_x, test_y, params, model_path=None, model_name=None):
    learning_rate = params['learning_rate']
    num_epochs = params['num_epochs']
    batch_size = params['batch_size']
    keep_prob_val = params['keep_prob_val']
    
    train_result, test_result = [], []
    tf.reset_default_graph()
    
    x = tf.placeholder(tf.float32, [None, train_x.shape[1], train_x.shape[2]], name="x")
    y = tf.placeholder(tf.float32, [None, train_y.shape[1]], name="y")
    #keep_prob = tf.placeholder(tf.float32, name="keep_prob")    
    
    
    logits, feature_layer = mcnnu.all_sensor_net(x, y, name="all_sensor_net")
    print("Logit shape: ",logits.get_shape().as_list())
    prediction = tf.nn.sigmoid(logits, name="prediction")
    correct_prediction = tf.equal(tf.greater(prediction, 0.5), tf.equal(y,1), name="correct_prediction")
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")
    
    loss_op = tf.reduce_mean(tf.nn.sigmoid_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")    
    train_step = tf.train.AdamOptimizer(learning_rate).minimize(loss_op, name="train_step")

    sess = tf.Session()
    sess.run(tf.global_variables_initializer())    
    train_x, train_y = mclfu.adjust_for_batch_size(train_x, train_y, batch_size)   
    
    train_count = len(train_x)
    for epoch in range(num_epochs):
        print("Epoch:", epoch)
        for ix in range(0, train_count, batch_size):                            
            batch_x, batch_y = train_x[ix:ix+batch_size], train_y[ix:ix+batch_size]
            sess.run(train_step, feed_dict={x:batch_x, y:batch_y})            
        
        #loss, acc = sess.run([loss_op, accuracy], feed_dict={x: train_x, y: train_y})        
        #print('  Train loss: {:.4f}, acc: {:.4f}'.format(loss, acc))
        
        if len(test_x)>0:
            loss, acc = sess.run([loss_op, accuracy], feed_dict={x: test_x, y: test_y})                
            print('  Test loss: {:.4f}, acc: {:.4f}'.format(loss, acc))
        
    print('!!!!!!!!!!!!!!! Optimization Finished !!!!!!!!!!!!!!!!!')
    
    if model_name:
        saver = tf.train.Saver()            
        mfileu.create_directory(model_path)
        saver.save(sess, model_path+'/'+model_name)    
        print("Model Saved!")
        
    sess.close()
        
    


# In[22]:


def get_train_data(exclude_subj):    
    w, l =[], []
    for subj in range(11):
        if subj==exclude_subj: 
            continue
            
        d = mfileu.read_file('windows_free_cnn', 'subj_'+str(subj)+".pkl")
        if len(w)==0:
            w, l = d["windows"], d["labels"]
        else:
            w = np.concatenate((w, d["windows"]), axis=0)
            l = np.concatenate((l, d["labels"]), axis=0)
    
    return w, l       
    


# In[23]:


subj, num_epochs = 100, 50
if 'C:' not in mfileu.get_path():    
    subj, num_epochs = int(sys.argv[1]), int(sys.argv[2])

print("\n============ Subject: {} =============".format(subj))
print("\n============ Epochs: {}  =============".format(num_epochs))

windows, labels = get_train_data(subj)
print("Shapes windows,labels, pos count:", windows.shape, labels.shape, np.sum(labels))


# In[24]:


windows, labels = shuffle(windows, labels)
train_x, val_x, train_y, val_y = train_test_split(windows, labels, test_size=0.1, stratify=labels)
print("train shapes: ", train_x.shape, train_y.shape, np.sum(train_y))
print("val shapes: ", val_x.shape, val_y.shape, np.sum(val_y))


# In[25]:


params={}
params['learning_rate'] = 0.001
params['num_epochs'] = num_epochs
params['batch_size'] = 128
params['keep_prob_val'] = 0.5
path = mfileu.get_path()
train_test_model(train_x, train_y, val_x, val_y, params, path+"/feature_models", "subj_"+str(subj))

