
# coding: utf-8

# In[ ]:


import numpy as np


# In[ ]:


def cluster_bites_by_minute(indices, maxDistance=16*60):    
    count = len(indices)
    if count<=1:
        return indices
    
    clusters = []
    j = 0
    for i in range(1, count):
        if  indices[i, 0] - indices[i-1, 1]>maxDistance:
            si = indices[j,0]
            ei = indices[i-1,1]            
            clusters.append([si, ei, i-j])
            j = i
    
    si = indices[j,0]
    ei = indices[count-1,1]    
    clusters.append([si, ei, count-j])
    clusters = np.array(clusters).astype(int)    
    return clusters


# In[3]:


def merge_clusters(indices, mergeDistance):    
    count = len(indices)
    if count<=1:
        return indices
    
    clusters = []
    j = 0
    for i in range(1, count):
        if  indices[i, 0] - indices[i-1, 1]>mergeDistance:
            si = indices[j,0]
            ei = indices[i-1,1]
            bite_count = np.sum(indices[j:i, -1])
            clusters.append([si, ei, bite_count])
            j = i
    
    si = indices[j,0]
    ei = indices[count-1,1]
    bite_count = np.sum(indices[j:count, -1])
    clusters.append([si, ei, bite_count])
    clusters = np.array(clusters).astype(int)    
    return clusters


# def cluster_gestures_by_minute(bite_indices, meal_indices, maxDistance=16*60):        
#     res = []
#     
#     b = np.zeros((len(bite_indices), 1)) + 1
#     bix = np.concatenate((bite_indices, b), axis=1)
#     
#     m = np.zeros((len(meal_indices), 1)) + 2
#     mix = np.concatenate((meal_indices, m), axis=1)
#     
#     indices = np.concatenate((mix, bix))
#     ix = np.argsort(indices[:, 0])
#     indices = indices[ix, :]
#     indices = indices.astype(int)
#     
#     assert indices.shape[1]==3
#     assert len(indices) == len(meal_indices)+len(bite_indices)
#     for i in range(1, len(indices)):
#         assert indices[i,0]>=indices[i-1, 0]
#         
#     count = len(indices)
#     clusters = []
#     j = 0
#     for i in range(1, count):
#         if  indices[i, 0] - indices[i-1, 1]>maxDistance:
#             si = indices[j,0]
#             ei = indices[i-1,1]            
#             bc = np.sum(indices[j:i, 2]==1)
#             mc = np.sum(indices[j:i, 2]==2)
#             clusters.append([si, ei, bc, mc])
#             j = i
#     
#     si = indices[j,0]
#     ei = indices[count-1,1]    
#     bc = np.sum(indices[j:count, 2]==1)
#     mc = np.sum(indices[j:count, 2]==2)
#     clusters.append([si, ei, bc, mc])
#     clusters = np.array(clusters).astype(int)
#     
#     #print(np.sum(clusters[:, 2]), len(meal_indices))
#     #print(np.sum(clusters[:, 3]), len(bite_indices))        
#     assert np.sum(clusters[:, 2]) == len(bite_indices)
#     assert np.sum(clusters[:, 3]) == len(meal_indices)
#     
#     return clusters

# def find_meals_steven(clusters):
#     cond = (clusters[:, 2]>=1) | ( (clusters[:, 1]-clusters[:, 0]) >= 5*60*16 )
#     clusters = clusters[cond]
#     return clusters

# def find_meals(clusters, minCount, mergeDistance=0):
#     assert 3<=clusters.shape[1]<=4
#     
#     if clusters.shape[1]==3:
#         counts = clusters[:, 2]
#         core_pt = (counts>=minCount)
#     else:
#         counts = clusters[:, 2] + clusters[:, 3]        
#         cond1 = (counts>=minCount) 
#         cond2 = (clusters[:, 2]>=1) | ( (clusters[:, 1]-clusters[:, 0])>=5*60*16 )
#         core_pt = cond1 & cond2
#     
#     if mergeDistance == 0:
#         clusters = clusters[core_pt]    
#     else:
#         clusters = merge_clusters(clusters, core_pt, mergeDistance)
#     
#     return clusters
#     
#     

# def merge_clusters(clusters, core_pt, distance):
#     if len(clusters)==0:
#         return clusters
#     
#     cluster_count = len(clusters)
#     
#     res = []    
#     si, last_cpi  = -1, -1        
#     for i in range(0, cluster_count):        
#         if last_cpi < 0:            
#             if core_pt[i]:
#                 last_cpi = i
#                 si = i
#                 while si>=0 and (clusters[i, 0] - clusters[si, 1])<=distance:
#                     si -= 1
#                 si +=1
#                 
#             continue
#             
#             
#         if clusters[i, 0] - clusters[last_cpi, 1] > distance:
#             if si>=0 and ( (not core_pt[i]) or (clusters[i, 0] - clusters[i-1, 1] > distance) ):
#                 res.append([si, i-1])                
#                 si = -1
#             
#             if core_pt[i] and si<0:                
#                 si = i 
#                 while si>=0 and (clusters[i, 0] - clusters[si, 1])<=distance:
#                     si -= 1                          
#                 si +=1
#         
#         if core_pt[i]:
#             last_cpi = i    
#     
#     if si>=0:
#         res.append([si, len(clusters)-1])                
#     
#     res = np.array(res).astype(int)    
#     meals = np.zeros(res.shape).astype(int)    
#     for i in range(res.shape[0]):
#         meals[i, 0] = clusters[res[i, 0], 0]
#         meals[i, 1] = clusters[res[i, 1], 1]
#         
#     return meals
#     

# In[ ]:






























































