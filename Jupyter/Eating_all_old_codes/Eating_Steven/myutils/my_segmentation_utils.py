
# coding: utf-8

# In[1]:


import numpy as np


# In[ ]:


def filter_segment_by_length(segments, min_len):
    s = segments
    cond = (s[:,1]-s[:,2]+1)>=min_len
    s = s[cond, :]
    return s  


# In[2]:


def segments_by_xth(x, xth):
    #return segs as indices
    count = len(x)
            
    y = (x<=xth).astype(int)    
    y = y[:-1] - y[1:]
    
    inside = 1 + np.argwhere(y==-1)
    outside = np.argwhere(y==1)
    #print(inside.shape, outside.shape)
    
    if inside[0]>outside[0]:
        outside = outside[1:]
    
    if inside[-1]>outside[-1]:
        inside = inside[:-1]
        
    assert len(inside)==len(outside)   
    res = np.concatenate((inside, outside), axis=1).astype(int)    
    return res


# In[3]:


def find_min_points_by_xth(x, xth, min_bite_interval):    
    step_length = min_bite_interval//2
    
    count = len(x)    
    mp = []
    for i in range(0, count-step_length, step_length):
        min_index = i + np.argmin(x[i:i+step_length])
        
        if x[min_index] <= xth:
            mp.append(min_index)
            
    return mp


# In[1]:


def filter_min_points_by_neighbor(mp, accel, params):
    if len(mp)<=1:
        return mp
    
    min_bite_interval = params["min_bite_interval"]
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right    
    step_length = min_bite_interval//2
    
    x = accel[:, 1]
    while True:
        res = []
        count = len(mp)
        ix = mp[0]
        ixRight = mp[1]
        if ixRight - ix > min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)
        
        for i in range(1, count - 1):
            ix = mp[i]
            ixLeft = mp[i - 1]
            ixRight = mp[i + 1]

            cond_left = ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]
            cond_right = ixRight - ix > min_bite_interval or x[ix] < x[ixRight]        

            if cond_left and cond_right:
                res.append(ix)
        
        ix = mp[count - 1]
        ixLeft = mp[count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]:
            res.append(ix)            
        
        if len(mp) == len(res):
            break        
        mp = res
        #print("     mp count: ", len(mp))
    
    return res


# In[ ]:


def remove_min_points_at_boundary(mps, data_len, offset):    
    si, ei = 0, len(mps)-1
    mp_count = len(mps)
    
    while si<mp_count and mps[si]-offset<0:
        si += 1
    
    while ei>=0 and mps[ei]+offset>=data_len:
        ei -= 1
        
    mps = mps[si:ei+1]
    return mps
    


# In[ ]:


def filter_min_points_by_feature(mp, accel, params):
    var_th = params["var_th"]
    window_len_left = params["window_len_left"]
    window_len_right = params["window_len_right"]
    window_len = window_len_left + window_len_right
    
    res = [];
    count = len(mp)
    for i in range(count):
        ix = mp[i]
        v = np.sum(np.var(accel[ix-window_len_left:ix+window_len_right+1, 1:], axis = 0))
        if v >= var_th:
            res.append(ix)
    return res    

