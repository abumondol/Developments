
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import matplotlib.pyplot as plt


# In[3]:


with open('C:/ASM/DevData/eating_steven/data/lab_data_steven.pkl', 'rb') as file:
    data = pickle.load(file)
    
with open('C:/ASM/DevData/eating_steven/data/lab_windows_steven.pkl', 'rb') as file:
    windows = pickle.load(file)    


# In[27]:


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


# In[28]:


for subj in range(7):
    train_x, train_y = get_train_windows_labels(data, windows, exclude_subj=subj, vth=0.5, neg_step = 4)
    print(train_x.shape, train_y.shape, np.sum(train_y==0), np.sum(train_y>0), np.sum(train_y==1), np.sum(train_y==2))
    

