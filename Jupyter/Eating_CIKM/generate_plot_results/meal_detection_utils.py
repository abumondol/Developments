
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def cluster_bites_by_minute(indices, maxDistance=16*60):    
    count = len(indices)
    if count==0:
        return []
    if count==1:
        return np.array([indices[0], indices[0], 1]).reshape((-1, 3))
    
    clusters = []
    j = 0
    for i in range(1, count):
        if  indices[i] - indices[i-1]>maxDistance:
            si = indices[j]
            ei = indices[i-1]            
            clusters.append([si, ei, i-j])
            j = i
    
    si = indices[j]
    ei = indices[count-1]    
    clusters.append([si, ei, count-j])
    clusters = np.array(clusters).astype(int)    
    return clusters


# In[3]:


def filter_clusters(clusters, minDuration, minCount):
    if len(clusters)==0:
        return clusters
    #print(clusters.shape)
    cond1 = (clusters[:,1]-clusters[:, 0])>=minDuration
    cond2 = clusters[:,2]>=minCount
    return clusters[cond1 & cond2]


# In[4]:


def merge_clusters(clusters, mergeDistance):
    if mergeDistance==0 or len(clusters)<=1:
        return clusters
    
    count = len(clusters)    
    
    res = []
    j = 0
    for i in range(1, count):
        if  clusters[i, 0] - clusters[i-1, 1]>mergeDistance:
            si = clusters[j,0]
            ei = clusters[i-1,1]
            bite_count = np.sum(clusters[j:i, -1])
            res.append([si, ei, bite_count])
            j = i
    
    si = clusters[j,0]
    ei = clusters[count-1,1]
    bite_count = np.sum(clusters[j:count, -1])
    res.append([si, ei, bite_count])
    res = np.array(res).astype(int)    
    return res

