
# coding: utf-8

# In[1]:


import numpy as np


# In[2]:


def cluster_bites_by_max_distance(indices, max_distance, min_bite_count):    
    count = len(indices)
    if count==0:
        return []
    if count==1:
        return np.array([indices[0], indices[0], 1]).reshape((-1, 3))
    
    clusters = []
    j = 0
    for i in range(1, count):
        if  indices[i] - indices[i-1]>max_distance:
            si = indices[j]
            ei = indices[i-1]            
            clusters.append([si, ei, i-j])
            j = i
    
    si = indices[j]
    ei = indices[count-1]    
    clusters.append([si, ei, count-j])
    clusters = np.array(clusters).astype(int)
    
    cond = clusters[:,2]>=min_bite_count
    clusters= clusters[cond]

    return clusters


# In[4]:


def merge_clusters(clusters, merge_distance):
    if merge_distance==0 or len(clusters)<=1:
        return clusters
    
    count = len(clusters)    
    
    res = []
    j = 0
    for i in range(1, count):
        if  clusters[i, 0] - clusters[i-1, 1]>merge_distance:
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


# In[ ]:


def dbscan(bites, minPts, eps):  
    count = len(bites)
    nbc = np.zeros((count, 2))
    for i in range(count):
        j = i-1
        while j>=0 and bites[i] - bites[j]<=eps:
            j = j-1  
            
        nbc[i, 0] = j+1 
        
        j = i+1
        while j<count and bites[j] - bites[i]<=eps:
            j = j+1
            
        nbc[i, 1] = j-1 
            
            
    core_pt = ( (nbc[:, 1] - nbc[:,0] + 1) >=minPts )    
        
    clusters = []    
    si, last_cpi  = -1, -1
    for i in range(0, len(bites)):        
        if last_cpi < 0:            
            if core_pt[i]:
                last_cpi = i
                si = nbc[i, 0]    
                
            continue            
            
        if bites[i] - bites[last_cpi] > eps:
            if si>=0 and ( (not core_pt[i]) or (bites[i] - bites[i-1] > eps) ):
                clusters.append([si, i-1])                
                si = -1
                
            
            if core_pt[i] and si<0:                
                si = nbc[i, 0]
        
        if core_pt[i]:
            last_cpi = i    
    
    if si>=0:
        clusters.append([si, len(bites)-1])                
    
    bite_indices = np.array(clusters).astype(int)    
    sample_indices = np.zeros(bite_indices.shape).astype(int)        
    sample_indices[:, 0] = bites[bite_indices[:, 0]]
    sample_indices[:, 1] = bites[bite_indices[:, 1]]
        
    return bite_indices, sample_indices    

