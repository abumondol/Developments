
# coding: utf-8

# In[6]:


import numpy as np
import os
import sys


# In[7]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_feature_utils as mfeatu
import my_data_process_utils as mdpu


# In[30]:


def get_window_indices(dcount, a, win_size, step_size):    
    six = np.array(list(range(0, dcount-win_size, step_size))).astype(int)
    assert six[-1]+win_size<=dcount
    cix = six + win_size//2 
    eix = six + win_size-1
    
    count = len(six)
    labels = np.zeros((count, ))
    episode_duration = np.zeros((count, ))
    onborder = np.zeros((count, ))
    
    if len(a)>0:        
        a = msfreeu.process_anntos(dcount, a)
        acount = len(a)
        print("Meal count: {}, Times data, last meal end: {}, {}".format(acount, dcount//16, a[-1,1]//16))    
        assert a[-1, 1]<=dcount-1        
        print(a)

        for i in range(acount):
            si, ei, mt = a[i, 0], a[i, 1], a[i, 2]        
            print('  Meal {}: {} - {}, {}'.format(i+1, si//16, ei//16, mt))

            cond = (cix>=si) & (cix<=ei)
            labels[cond] = mt

            cond1 =  ( (six<si) | (eix>ei) )
            onborder[cond & cond1] = -1

            cond1 = (cix<si) | (cix>ei)
            cond2 = (six>=si) & (six<=ei)
            cond3 = (eix>=si) & (eix<=ei)
            onborder[cond1 & (cond2 | cond3)] = -2
            
            episode_duration[:] = ei-si+1
        
    
    res = np.zeros((count, 6))
    res[:, 0] = six
    res[:, 1] = eix
    res[:, 2] = cix    
    res[:, 3] = episode_duration
    res[:, 4] = onborder
    res[:, 5] = labels
    
    print("total, onborders:", count, np.sum(onborder>=0))
    
    return res.astype(int)    


# In[31]:


#This function generates window indices for positive and negative segments separately
def get_window_indices_neg_pos(dcount, a, neg_step, pos_step):    
    a = msfreeu.process_anntos(dcount, a)            
    acount = len(a)
    print("Meal count: {}, Times data, last meal end: {}, {}".format(acount, dcount//16, a[-1,1]//16))    
    assert a[-1, 1]<=dcount-1        
    print(a)
    
    si, ei = 0, a[0,0]    
    indices = get_indices(si, ei, neg_step)
    windows = np.zeros((indices.shape[0], 3), dtype=np.int32)
    windows[:, 0] = indices        
    
    for i in range(acount):
        si, ei, mt = a[i, 0], a[i, 1], a[i, 2]        
        print('  Meal {}: {} - {}, {}'.format(i+1, si//16, ei//16, mt))
        
        print("    Positives: ")        
        indices = get_indices(si, ei, pos_step)
        w = np.zeros((indices.shape[0], 3), dtype=np.int32)
        w[:, 0] = indices
        w[:, 1] = (ei-si)//16        
        w[:, 2] = mt        
        windows = np.concatenate((windows, w), axis=0)            
        print("\tShapes windows: ", w.shape)

        # Negative windows
        if ei == dcount-1:
            break
            
        print("    Negatives: ")
        si = ei+1
        ei = dcount-1 if i==acount-1 else a[i+1, 0]-1
        
        print("\tTimes: ", si//16, " - " ,ei//16)
        
        indices = get_indices(si, ei, neg_step)
        w = np.zeros((indices.shape[0], 3), dtype=np.int32)
        w[:, 0] = indices    
        windows = np.concatenate((windows, w), axis=0)        
        print("\tShapes window: ", w.shape)        
    
    return windows
   


# In[32]:


def get_window_indices_all(ds, annots, win_size, step_size, subject=-1, exclude=True, blockPrint=True):    
    if blockPrint:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating window indices...")
    indices = []
    for subj in range(len(ds)):
        if (exclude and subj==subject) or ((not exclude) and subj != subject):
            continue
            
        for sess in range(len(ds[subj])):
            print("\nSubject, Session: ", subj, sess)
            dcount = len(ds[subj][sess]) 
            a = annots[subj][sess]
            
            ix = get_window_indices(dcount, a, win_size=win_size, step_size=step_size)            
            ix = mdpu.add_subj_sess_to_array(ix, subj, sess, at_begin=True, to_int=True)
            
            indices = ix if len(indices) == 0 else np.concatenate((indices, ix), axis=0)
            print("Session Shapes window, labels: ", ix.shape, np.sum(ix[:,-1]==0), np.sum(ix[:,-1]==1), np.sum(ix[:,-1]==2), np.sum(ix[:,-1]==3))
            print("---------------------------------------------")
        
    if blockPrint:
        sys.stdout.close()
        #sys.stdout = sys.__stdout__
        sys.stdout = old_stdout
        
    return indices.astype(int)


# In[33]:


def get_window_data(ds, indices, win_size):   
    count = len(indices)    
    w = np.zeros((count, win_size, 9))
    for i in range(count):
        subj, sess, si = indices[i, 0], indices[i, 1], indices[i, 2]        
        w[i, :, :] = ds[subj][sess][si:si+win_size, 4:]        
    return w


# In[34]:


def get_window_data_accel(ds, indices, win_size):   
    count = len(indices)    
    w = np.zeros((count, win_size, 3))
    for i in range(count):
        subj, sess, si = indices[i, 0], indices[i, 1], indices[i, 2]
        w[i, :, :] = ds[subj][sess][si:si+win_size, 1:4]        
    return w


# hand = 'right'
# ds = mfileu.read_file('data', 'free_data_steven_'+hand+'.pkl')
# annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')

# w = get_window_indices_all(ds, annots, win_size=10*16, step_size=8, blockPrint=False)
# print("All Shapes window, labels: ", w.shape, np.sum(w[:,-1]==0), np.sum(w[:,-1]==1), np.sum(w[:,-1]==2), np.sum(w[:,-1]==3))
