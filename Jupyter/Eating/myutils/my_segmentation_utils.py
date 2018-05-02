
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def filter_segment_by_length(segments, min_len):
    s = segments
    cond = (s[:,1]-s[:,2]+1)>=min_len
    s = s[cond, :]
    return s  


# In[3]:


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


# In[8]:


def filter_min_points_by_neighbor(x, mps, min_bite_interval):
    if len(mps)<=1:
        return mps
    
    while True:
        res = []
        count = len(mps)
        ix = mps[0]
        ixRight = mps[1]
        if ixRight - ix >= min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)
        
        for i in range(1, count - 1):
            ix = mps[i]
            ixLeft = mps[i - 1]
            ixRight = mps[i + 1]

            cond_left = ix - ixLeft >= min_bite_interval or x[ix] < x[ixLeft]
            cond_right = ixRight - ix >= min_bite_interval or x[ix] < x[ixRight]        

            if cond_left and cond_right:
                res.append(ix)
        
        ix = mps[count - 1]
        ixLeft = mps[count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] < x[ixLeft]:
            res.append(ix)            
        
        if len(mps) == len(res):
            break        
        mps = res
        
    res = np.array(res).astype(int)
    return res


# In[ ]:


def filter_min_points_by_min_stay(x, mps, xth, min_stay):
    xcount = len(x)
    mps = mps[(mps>=min_stay) & (mps<xcount-min_stay)]
    flags = np.ones((len(mps), ))
    
    for i in range(len(mps)):
        stay_count = 0
        j = mps[i]
        while stay_count<min_stay and x[j]<=xth:
            j+=1
            stay_count+=1
        
        if stay_count>=min_stay:
            continue
            
        j = mps[i]-1
        while stay_count<min_stay and x[j]<=xth:
            j-=1
            stay_count+=1
            
        if stay_count<min_stay:
            flags[i] = 0
            
    mps = mps[flags==1]
    return mps


# In[9]:


def find_min_points_by_xth(x, xth, min_bite_interval, min_stay=0, boundary_offset = 0, filter_by_neighbor = True):    
    step_length = min_bite_interval//2
    
    count = len(x)    
    mps = []
    for i in range(0, count-step_length, step_length):
        min_index = i + np.argmin(x[i:i+step_length])
        
        if x[min_index] <= xth:
            mps.append(min_index)
            
    mps = np.array(mps).astype(int)
    if filter_by_neighbor:
        mps = filter_min_points_by_neighbor(x, mps, min_bite_interval)
    
    if boundary_offset>0:        
        mps = mps[(mps>=boundary_offset) & (mps<len(x)-boundary_offset)]
        
    if min_stay>0:        
        mps = filter_min_points_by_min_stay(x, mps, xth, min_stay)
        
    return mps


# In[6]:


def remove_min_points_at_boundary(mps, data_len, offset):        
    mps = mps[mps>offset and mps<data_len-offset]    
    return mps
    


# In[7]:


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

