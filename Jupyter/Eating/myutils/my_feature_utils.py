
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp


# In[2]:


def get_variance_accel(ds, indices, win_size):   
    count = len(indices)
    v = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        v[i] = np.sum(np.var(ds[subj][sess][ix:ix+win_size, 1:4], axis=0))                
    return v
    


# In[3]:


def get_grav_x(ds, indices, index_offset=0):   
    count = len(indices)
    x = np.zeros((count, ))    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]+index_offset
        x[i] = ds[subj][sess][ix, -3]                
    return x


# In[11]:


def get_covariance_multi_sensor(w):
    covs = np.zeros((w.shape[1], ))
    for i in range(0, w.shape[1], 3):
        d = w[:, i:i+3]
        covs[i:i+3] = np.cov(d, rowvar=False).diagonal()
    return covs


# In[12]:


def get_features(w):
    mean = np.mean(w, axis=0)
    std = np.std(w, axis=0)
    cov = get_covariance_multi_sensor(w)
    skewness = sp.stats.skew(w, axis=0)
    kurtosis = sp.stats.kurtosis(w, axis=0)
    f = np.concatenate((mean, std, cov, skewness, kurtosis))
    return f


# In[2]:


def get_energy(d, step_size):
    dcount = len(d)
    indices = list(range(0, dcount-step_size, step_size))
    res = np.zeros((len(indices), 3))    
    for i in range(len(indices)):
        ix = indices[i]        
        res[i, 0] = ix
        
        seg = d[ix:ix+step_size, 4:7]        
        res[i, 1] = np.sum(seg*seg)
        
        seg = d[ix:ix+step_size, 7:10]        
        res[i, 2] = np.sum(seg*seg)
    
    return res
                

        
        

