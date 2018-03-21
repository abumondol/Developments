
# coding: utf-8

# In[10]:

import numpy as np
from sklearn.cluster import KMeans


# In[11]:

#print(sub_dict[1][0][:100, :])
def boundaries(data):
    d = data[:, -3:]
    km = KMeans(n_clusters = 2)
    res = km.fit_predict(d)    
    si = 0    
    for i in range(len(d)-1):
        if res[i] != res[i+1]:
            si = i + 1
            break
            
    ei = len(d)-1    
    for i in range(len(d)-1, 0, -1):
        if res[i] != res[i-1]:
            ei = i - 1
            break         
    
    return si, ei


# In[12]:

def normalize_data(data, si=0, ei=0):
    if ei==0:
        ei=len(data)
        
    data = np.copy(data)
    m = np.mean(data[si:ei, 1:], axis = 0)
    s = np.std(data[si:ei, 1:], axis = 0)
    
    data[:, 1:] = (data[:, 1:] - m)/s
    return data


# In[ ]:

def DTW(a, b):
    n = len(a)
    m = len(b)        
        
    D = np.zeros((n+1, m+1)) + 1e10            
    for i in range(1, n+1):
        for j in range(1, m+1):
            d = a[i-1, :] - b[j-1, :]
            D[i, j] = np.sum(d*d) + min([D[i-1, j-1], D[i, j-1], D[i-1, j]])
            
    distance = d[n, m]        
    return distance
    


# In[ ]:

def DTW(a, b, a_indices, b_indices):
    a_six, a_eix = a_indices
    b_six, b_eix = b_indices
    
    n = a_eix - a_six + 1
    m = b_eix - b_six + 1        
        
    D = np.zeros((n+1, m+1)) + 1e10            
    for i in range(1, n+1):
        for j in range(1, m+1):
            d = a[a_six + i-1, :] - b[b_six + j-1, :]
            D[i, j] = np.sum(d*d) + min([D[i-1, j-1], D[i, j-1], D[i-1, j]])
            
    distance = d[n, m]        
    return distance    


# In[13]:

def DTW_invariance(a, b, si, ei):
    n = len(a)
    m = len(b)        
        
    D = np.zeros((n+1, m+1)) + 1e10        
    
    if si >0:
        D[0, 0:si] = 0
    
    for i in range(1, n+1):
        for j in range(1, m+1):
            d = a[i-1, :] - b[j-1, :]
            D[i, j] = np.sum(d*d) + min([D[i-1, j-1], D[i, j-1], D[i-1, j]])
    
    if m-1>ei:
        distance = np.amin(d[n, ei:])
    else:
        distance = d[n, m]
        
    return distance


# In[ ]:

def DTW_step(a, b, a_indices, b_indices, step_size):
    
    bmin_ix, b_six, b_eix, bmax_ix = b_indices
    
    res = []    
    for s in range(b_six, bmin_ix-1, -step_size):
        e = s + b_eix - b_six
        r = DTW(a, b, a_indices, [s, e])
        res.append(r)
        
    for s in range(b_six, bmax_ix+1, step_size):
        e = s + b_eix - b_six
        r = DTW(a, b, a_indices, [s, e])
        res.append(r)
    
    return min(res)

