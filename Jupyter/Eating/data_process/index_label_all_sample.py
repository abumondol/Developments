
# coding: utf-8

# In[ ]:


import numpy as np
import pickle
import os
import sys


# In[ ]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[ ]:


lab_free = 'lab'
assert lab_free in ['lab', 'free']

if lab_free=='lab':
    ds = mfileu.read_file('data', 'lab_data_combo_right.pkl')
    ds_smooth = mfileu.read_file('data', 'lab_data_combo_right_smoothed.pkl')
    annots = mfileu.read_file('data', 'lab_annots_combo_right.pkl')
    ba = mfileu.read_file('data', 'lab_data_combo_blank_array.pkl')
else:
    ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
    ds_smooth = mfileu.read_file('data', 'free_data_steven_right_smoothed.pkl')
    annots = mfileu.read_file('data', 'free_annots_steven_processed.pkl')
    ba = mfileu.read_file('data', 'free_data_steven_blank_array.pkl')
        
print(lab_free, ", Subject Count:", len(ds))


# In[ ]:


win_size=5*16
step_size=1


# In[ ]:


def get_labels_lab_uva(indices, a):    
    wcount, acount = len(indices), len(a)  
    
    si = indices
    q1 = si + win_size//4
    mi = si + 2*win_size//4    
    q3 = si + 3*win_size//4
    ei = si + win_size-1
    
    labels=np.zeros((wcount,))    
    for i in range(acount):         
        assert a[i,2] >= 1 #There is no zero       
        ix = a[i, 0]
        label = a[i,2]
        
        if label==1:
            cond = (si<=ix) & (ix<=ei)
            labels[cond] = -1
        
        cond = (q1<=ix) & (ix<=q3)        
        labels[cond] = label
        
    return np.array(labels)


# In[ ]:


def get_labels_lab_steven(indices, a):    
    wcount, acount = len(indices), len(a)  
    
    si = indices
    q1 = si + win_size//4
    mi = si + 2*win_size//4    
    q3 = si + 3*win_size//4    
    ei = si + win_size-1
    
    labels=np.zeros((wcount,))    
    for i in range(acount):
        assert a[i,3]!=2 # Not left hand
        assert 1<=a[i, 2]<=2 
        
        if a[i, 2]==1: #bite
            ix = a[i, 0]
            cond = (si<=ix) & (ix<=ei)
            labels[cond] = -1
            cond = (q1<=ix) & (ix<=q3)
            labels[cond] = 1
            
        else:#sip
            ix1 = a[i, 0]
            ix2 = a[i, 1]
            cond = (mi>=ix1) & (mi<=ix2)
            labels[cond] = 2
        
    return np.array(labels)


# In[ ]:


for subj in range(len(ds)):    
    for sess in range(len(ds[subj])):
        print(subj, sess)        
        d = ds[subj][sess]
        dsm = ds_smooth[subj][sess]
        a = annots[subj][sess]        
        print("Bite, Sip count: ", np.sum(a[:,2]==1), np.sum(a[:,2]==2))
                
        count = (len(d)-win_size)//step_size + 1
        ssilvg = np.zeros((count, 6))
        ssilvg[:, 0] = subj
        ssilvg[:, 1] = sess
        
        for i in range(count):
            si = i*step_size                
            ssilvg[i, 2] = si        
            ssilvg[i, 4] = np.sum(np.var(d[si:si+win_size, 1:4], axis=0))
            ssilvg[i, 5] = dsm[si+(win_size//2), 1]
            
        if lab_free=='lab':
            if subj<=6:
                ssilvg[:, 3] = get_labels_lab_steven(ssilvg[:, 2], a)
            else:
                ssilvg[:, 3] = get_labels_lab_uva(ssilvg[:, 2], a)            
        
        ba[subj][sess] = ssilvg
        print("Shape, Count X, neg, sip, drink", ssilvg.shape, np.sum(ssilvg[:,3]==-1), np.sum(ssilvg[:,3]==0), np.sum(ssilvg[:,3]==1), np.sum(ssilvg[:,3]==2))


# In[ ]:


if lab_free=='lab':
    mfileu.write_file('ssilvg/ssilvg_all_samples', 'lab_ssilvg_combo_right.pkl', ba)
    mfileu.write_file('ssilvg/ssilvg_all_samples', 'lab_ssilvg_steven_right.pkl', ba[:7])
    mfileu.write_file('ssilvg/ssilvg_all_samples', 'lab_ssilvg_uva_right.pkl', ba[7:])    
else:
    mfileu.write_file('ssilvg/ssilvg_all_samples', 'free_ssilvg_steven_right.pkl', ba)

