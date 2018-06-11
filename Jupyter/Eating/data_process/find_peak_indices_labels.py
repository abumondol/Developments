
# coding: utf-8

# In[9]:


import numpy as np
import pickle
import os
import sys


# In[10]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu


# In[11]:


v_min, v_max = 1, 25
g_min, g_max = -10, -3
min_distance = 2*16
win_size = 10*16
half_win_size = win_size//2


# In[12]:


lab_free = 'lab'
ds = mfileu.read_file('data', '{}_data_steven_right_smoothed.pkl'.format(lab_free))
if lab_free=='lab':
    annots = mfileu.read_file('data', '{}_annots_steven_right.pkl'.format(lab_free))
else:
    annots = mfileu.read_file('data', '{}_annots_steven_processed.pkl'.format(lab_free))


# In[13]:


def find_labels_lab(gt, ix):
    cond = (gt[:, 2]==1) #bites
    bites = gt[cond, 0] #bite indices
    cond = (gt[:, 2]==2) #sips only
    sips = gt[cond, 0:2] #sip indices
    
    bite_count, sip_count, ix_count = len(bites), len(sips), len(ix)
    
    labels = np.zeros((ix_count, ))
    for i in range(bite_count):
        ix_left, ix_right = bites[i]-32, bites[i]+32
        cond = (ix>=ix_left) & (ix<=ix_right)
        labels[cond] = 1
        
    for i in range(sip_count):
        ix_left, ix_right = sips[i, 0], sips[i, 1]
        cond = (ix>=ix_left) & (ix<=ix_right)
        labels[cond] = 2
    
    return labels


# In[14]:


def find_labels_free(gt, ix):       
    gt_count, ix_count = len(gt), len(ix)
    
    labels=np.zeros((ix_count,))    
    for i in range(gt_count):                            
        ix1 = a[i, 0]
        ix2 = a[i, 1]
        cond = (ix>=ix1) & (ix<=ix2)
        labels[cond] = a[i, 2]
    
    return labels.astype(int)


# In[15]:


def find_peaks(g):        
    gl, gm, gr = g[:-2], g[1:-1],  g[2:]    
    cond1 = (gm<gl) & (gm<gr) 
    cond2 = (gm>=g_min) & (gm<=g_max)
    cond = cond1 & cond2
    ix = (np.array(np.nonzero(cond)) + 1).reshape((-1, ))        
    count = len(ix)
    
    ############ Select witn min g within 2 second
    flags = np.ones((count, ))
    for i in range(count):
        j = i+1
        while j<count and ix[j]-ix[i]<=min_distance:
            if g[ix[j]] < g[ix[i]]:
                flags[i] = 0
                break
            j+=1
    
        if flags[i]==1:
            j = i-1
            while j>=0 and ix[i]-ix[j]<=min_distance:
                if g[ix[j]] < g[ix[i]]:
                    flags[i] = 0
                    break
                j-=1
            

    ix = ix[flags==1]    
    
    ##### REMOVE NEAR bOUNDARY #########
    count = len(ix)
    i = 0
    while i<count and ix[i]<win_size//2:
        i+=1
        
    j = count-1
    boundary = len(g) -1 - win_size//2
    while j>=0 and ix[j]>boundary:
        j-=1
    
    #print(i,j, ix[i], ix[j])
    ix = ix[i:j+1]
    
    
    return ix


# In[16]:


res = []
for subj in range(len(ds)):
    for sess in range(len(ds[subj])):
        d = ds[subj][sess]
        a = annots[subj][sess]
        #print(a)
        
        ix = find_peaks(d[:, 1])
        count = len(ix)
        v = np.zeros((count, ))        
        for i in range(count):
            index = ix[i]
            v[i] = np.sum(np.var(d[index-40:index+40, 1:4], axis=0))
            
        cond = (v>=v_min) & (v<=v_max)
        ix = ix[cond]        
        print("\n", subj, sess, d.shape[0], count, len(ix))
        
        if lab_free=='lab':
            labels = find_labels_lab(a, ix)            
            print("Lab labels: ", len(labels), np.sum(labels==0), np.sum(labels==1))
        else:
            labels = find_labels_free(a, ix)
            print("Free labels:", len(labels), np.sum(labels==0), np.sum(labels==1), np.sum(labels==2), np.sum(labels==3))
        
        count = len(ix)
        r = np.zeros((count, 4))
        r[:, 0] = subj
        r[:, 1] = sess
        r[:, 2] = ix
        r[:, 3] = labels
        
        res = r if len(res)==0 else np.concatenate((res, r))
        print(r.shape, res.shape)

res = res.astype(int)


# In[17]:


mfileu.write_file('peaks', '{}_ssil_steven_right.pkl'.format(lab_free), res)


# In[18]:


print(res.shape, np.sum(res[:,-1]==0), np.sum(res[:,-1]==1), np.sum(res[:,-1]==2), np.sum(res[:,-1]==3))

