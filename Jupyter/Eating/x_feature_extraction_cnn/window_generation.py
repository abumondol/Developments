
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import os
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[3]:


ds = mfileu.read_file("data", "free_data_steven_right.pkl")
annots = mfileu.read_file("data", "free_data_steven_annots.pkl")


# In[4]:


def get_windows_segments(d, si, ei, win_size, step_size):
    #print("\tdcount, si, ei:", len(d), si, ei, si//16, ei//16)
    indices = list(range(si, ei-win_size, step_size))
    count = len(indices)
    w = np.zeros((count, win_size, d.shape[1]))
    j = 0
    for i in indices:
        w [j, :, :] = d[i:i+win_size, :]
        j += 1
    
    assert j == count
    return w
    
    
def get_windows(d, a, win_size=16, neg_step=8, pos_step=1):
    windows = np.empty((0, win_size, d.shape[1]))
    labels = np.empty((0, 1), dtype=np.int32)
    dcount = len(d)
    acount = len(a)
    
    for i in range(acount):
        if a[i, 0]>=dcount:
            print("Annots cut at meal: {}/{}, Times data, start: {}, {}".format(i+1, acount, dcount//16, a[i,0]//16))
            a = a[:i, :]            
            break
            
        if a[i, 1]>=dcount:
            print("Annots end adjusted at meal: {}/{}, Times data, end: {}, {}".format(i+1, acount, dcount//16, a[i,0]//16))
            a[i, 1]=dcount-1
            a = a[:i+1, :]            
            break
            
            
    acount = len(a)    
    print("Meal count: {}, Times data, last meal end: {}, {}".format(acount, dcount//16, a[-1,1]//16))    
    assert a[-1, 1]<=dcount-1
     
    
    si, ei = 0, a[0,0]
    w = get_windows_segments(d, si, ei, win_size, neg_step)
    l = np.zeros((len(w), 1), dtype=np.int32)
    windows = np.concatenate((windows, w), axis=0)    
    labels = np.concatenate((labels, l), axis=0)
    
    for i in range(acount):
        si = a[i, 0]
        ei = a[i, 1]
        mt = a[i, 2]        
        print('  Meal {}: {} - {}, {}'.format(i+1, si//16, ei//16, mt))
                
        #Positive Windows        
        print("    Positives: ")                
        
        if mt>2 or (mt==1 and ei-si>=30*60*16) or (mt==2 and ei-si>=20*60*16):
            print("\tExcluded => Meal Index:{}, Meal type:{}, Duration:{}".format(i, mt, (ei-si)/16))
        else:            
            w = get_windows_segments(d, si, ei, win_size, pos_step)
            l = np.zeros((len(w), 1), dtype=np.int32) + 1            
            windows = np.concatenate((windows, w), axis=0)            
            labels = np.concatenate((labels, l), axis=0)
            print("\tShapes window, label: ", w.shape, l.shape)
            
        # Negative windows
        if ei == dcount-1:
            break
            
        print("    Negatives: ")
        si = ei+1
        ei = dcount-1 if i==acount-1 else a[i+1, 0]-1
        
        print("\tTimes: ", si//16, " - " ,ei//16)
        
        w = get_windows_segments(d, si, ei, win_size, neg_step)
        l = np.zeros((len(w), 1), dtype=np.int32)
        windows = np.concatenate((windows, w), axis=0)        
        labels = np.concatenate((labels, l), axis=0)
        
        print("\tShapes window, label: ", w.shape, l.shape)        
        
    assert len(windows) == len(labels)
    return windows, labels
        
        
    
    


# In[5]:


win_size=16
for subj in range(len(ds)):
    windows = np.empty((0, win_size, 9))
    labels = np.empty((0, 1), dtype=np.int32)
    
    for sess in range(len(ds[subj])):
        print("\n\nSubject, Session: ", subj, sess)
        d = ds[subj][sess]
        d = d[:, 4:]
        
        d[:, :3] = mdpu.smooth_data(d[:, :3], 0.9)
        d[:, 3:6] = mdpu.smooth_data(d[:, 3:6], 0.9)
        
        a = annots[subj][sess]
        
        w, l = get_windows(d, a)        
        windows = np.concatenate((windows, w), axis=0)        
        labels = np.concatenate((labels, l), axis=0)        
        print("Session Shapes window, label: ", w.shape, l.shape)
        
    subj_data={"windows":windows, 'labels':labels}
    print("Subject Shapes window, label: ", windows.shape, labels.shape, np.sum(labels))    
    mfileu.write_file('windows_free_cnn', 'subj_'+str(subj)+".pkl", subj_data)
    print("---------------------------------------------")
        

