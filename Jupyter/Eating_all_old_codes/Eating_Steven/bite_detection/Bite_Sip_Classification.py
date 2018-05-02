
# coding: utf-8

# In[37]:


import numpy as np
import pickle
import os
import sys
#import matplotlib.pyplot as plt
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split


# In[44]:


from keras.layers import Conv2D, MaxPooling2D, Bidirectional, LSTM, Dense, Flatten, Dropout, Activation
from keras.models import Sequential, model_from_json, load_model


# In[39]:


path = 'C:/ASM/DevData/eating_steven' if 'C:' in os.getcwd() else '.'
with open(path + '/data/lab_data_steven.pkl', 'rb') as file:
    data = pickle.load(file)
    
with open(path + '/data/lab_windows_steven.pkl', 'rb') as file:
    windows = pickle.load(file)    


# In[40]:


def get_train_windows_labels(data, windows, exclude_subj, vth, neg_step):
    win_size=80    
    axis_count =9
    half_win_size = win_size//2
    
    train_x = np.zeros((0, win_size, axis_count))
    train_y = np.zeros((0))
    
    print("Generating Train data ...")
    for subj in range(7):
        if subj==exclude_subj: continue            
        for sess in range(len(data[subj])):            
            print(subj, sess, end=" | ")
            
            d = data[subj][sess]["data_right"]
            mn, mx = -2*9.8, 2*9.8
            d[:, 1:4] = (np.clip(d[:, 1:4], mn, mx)-mn)/(mx-mn)

            mn, mx = -15, 15
            d[:, 4:7] = (np.clip(d[:, 4:7], mn, mx)-mn)/(mx-mn)


            indices = windows[subj][sess]["w_indices"]
            labels = windows[subj][sess]["labels"]

            v = indices[:, -1]
            l = labels[:, 0]        
            cond = (v>=vth) & (l>=0)

            indices = indices[cond, :]
            labels = labels[cond, :]
            l = labels[:, 0]                    
            cond_pos = (l>0)
            cond_neg = (l==0)

            indices_pos = indices[cond_pos, :]
            labels_pos = labels[cond_pos, :]
            
            indices_neg = indices[cond_neg, :]
            labels_neg = labels[cond_neg, :]
            wcount = len(indices_neg)        
            selected = np.arange(0, wcount, neg_step).astype(int)            
            assert len(selected) == (wcount-1)//neg_step+1
            
            indices_neg = indices_neg[selected, :]
            labels_neg = labels_neg[selected, :]      
            
            assert np.sum(labels_neg[:,0]) == 0
            assert np.sum(labels_pos[:,0]==0) == 0

            indices= indices_pos
            wcount = len(indices)        
            tx_pos = np.zeros((wcount, win_size, axis_count))        
            for i in range(wcount):
                j = int(indices[i, 0])
                tx_pos[i, :, :] = d[j-half_win_size:j+half_win_size, 1:]

            indices= indices_neg
            wcount = len(indices)        
            tx_neg = np.zeros((wcount, win_size, axis_count))        
            for i in range(wcount):
                j = int(indices[i, 0])
                tx_neg[i, :, :] = d[j-half_win_size:j+half_win_size, 1:]
                
            train_x = np.concatenate((train_x, tx_pos, tx_neg), axis=0)
            train_y = np.concatenate((train_y, labels_pos[:, 0], labels_neg[:, 0]))
            
            assert len(train_x) ==len(train_y)
    
    print("")        
    train_y = train_y.reshape((-1, 1))    
    return train_x, train_y


# In[49]:


def train_blstm_keras(train_x, train_y, test_x, test_y, epochs=100, batch_size=128):
    model = Sequential()
    model.add(Bidirectional(LSTM(64, return_sequences=True),
                            input_shape=(train_x.shape[1], train_x.shape[2])))
    model.add(Bidirectional(LSTM(64)))
    model.add(Dense(32))
    model.add(Dropout(0.5))
    model.add(Dense(1))
    model.add(Activation('sigmoid'))
    model.compile(loss='binary_crossentropy', optimizer='rmsprop')
    
    if len(test_x)==0:
        model.fit(train_x, train_y,
                  batch_size=batch_size,                  
                  epochs=epochs)
    else:
        model.fit(train_x, train_y,
                  batch_size=batch_size,
                  validation_data = [test_x, test_y],
                  epochs=epochs)
    return model


# In[50]:


subj = int(sys.argv[1])
num_epochs = 40


print("Lopo Subject: ", subj)

train_x, train_y = get_train_windows_labels(data, windows, exclude_subj=subj, vth=0.5, neg_step = 4)
print("Shapes train_x, train_y: ", train_x.shape, train_y.shape)
print("Count No bite, bite/sip, bite, sip: ", np.sum(train_y==0), np.sum(train_y>0), np.sum(train_y==1), np.sum(train_y==2))
train_x, train_y = shuffle(train_x, train_y)
train_x, val_x, train_y, val_y = train_test_split(train_x, train_y, test_size=0.1, stratify=train_y)

train_y[train_y==2] = 1
val_y[val_y==2] = 1
print("train, val:", train_y.shape, val_y.shape, np.sum(train_y==0), np.sum(train_y==1), np.sum(val_y==0), np.sum(val_y==1))
model = train_blstm_keras(train_x, train_y, val_x, val_y, epochs=num_epochs, batch_size=128)
model.save(path+'/models/kera_blstm_lab_'+str(subj)+'.h5')

