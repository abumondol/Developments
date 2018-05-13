
# coding: utf-8

# In[7]:


import numpy as np
import os
import sys


# In[8]:


def cluster_bites(indices, pred):
    count = len(indices)
    assert count == len(pred)
    
    cond = (pred[:, 0]>=0.5)
    pred = pred[cond]
    indices = indices[cond]
    count = len(indices)
        
    clusters=[]
    c = [indices[0]]
    for i in range(1, count):
        if  indices[i] - c[-1]<=16*60:
            c.append(indices[i])
        else:
            clusters.append([c[0], c[-1], len(c)])
            c=[indices[i]]
    clusters.append([c[0], c[-1], len(c)])
    
    return np.array(clusters)
        


# In[17]:


def smooth_free(indices, pred):
    count = len(indices)
    assert count == len(pred)
    
    offset = 30
    res = []    
    for i in range(offset, count-offset, offset):
        mean = np.mean(pred[i-offset:i+offset])
        res.append([indices[i], mean])
            
    return np.array(res)


# In[10]:


def find_clusters_free(indices, pred, min_distance=5*60*16):
    p = smooth_free(indices, pred)        
    indices = p[p[:, 1]>=0.5, 0]
    
    count = len(indices)    
    if count==0:
        return []
    
    clusters=[]
    c = [indices[0]]    
    for i in range(1, count):
        #print(indices.shape, len(c))
        #print(indices[i], c[-1])
        
        if  indices[i] - c[-1]<=min_distance:
            c.append(indices[i])
        else:
            clusters.append([c[0], c[-1], len(c)])
            c=[indices[i]]
            
    clusters.append([c[0], c[-1], len(c)])            
    return np.array(clusters)


# In[11]:


def find_meal_result(gt, clusters, min_count = 5):
    cond = (clusters[:, 2]>=min_count) #& (clusters[:, 1]-clusters[:, 0]>=16*60*2)
    cl = clusters[cond, :]
    gtm = gt[gt[:, 2]==1, :]
    
    acov = np.zeros((len(gtm), 6))     
    # 0:coverd_by_count, 1:start_error, 2:end_error, 3:gap_error, 4:cover_duration, 5:gt_duration
    
    for i in range(len(gtm)):
        sig, eig = gtm[i, 0], gtm[i, 1]
        for j in range(len(cl)):
            sic, eic = cl[j, 0], cl[j, 1]
            
            if eig<sic:
                break                
            if sig>eic:
                continue
                
            acov[i, 0] = acov[i, 0] + 1
            
            if acov[i, 0]==1:
                acov[i, 1] = sic - sig #start error            
            
            acov[i, 2] = eic - eig #end error            
            
            if acov[i, 0]>1:
                acov[i, 3] = sic - cl[j-1, 1] #gap error
            
            acov[i, 4] = acov[i, 4] + (eic - sic+1) #cover_duration
            acov[i, 5] = eig - sig+1 #gt_duration
            
     
    clcov = np.zeros((len(cl), 3))
    # 0:coverd_by_type
    for j in range(len(cl)):
        sic, eic = cl[j, 0], cl[j, 1]
        for i in range(len(gt)):
            sig, eig, mt = gt[i, 0], gt[i, 1], gt[i, 2]
            
            if eic<sig:
                break                
            if sic>eig:
                continue
                
            clcov[j, mt-1] = 1
            
        
    return acov, clcov
        


# In[12]:


def filter_by_var_gx(pred, var, gx, hand):
    vth_min, vth_max, xth = 1, 50, 0
    if hand=='right':
        cond = (var>=vth_min)&(var<=vth_max)&(gx<=xth)
    else:
        cond = (var>=vth_min)&(var<=vth_max)&(gx>=xth)

    cond = np.logical_not(cond)        
    pred[cond] = 0
    return pred


# In[ ]:


def get_meal_detection_results(gt, clusters):
    acov = np.zeros((len(gt), 4))         
    # 0:coverd_by_count, 1:start_error, 2:end_error, 3:gap_error, 4:cover_duration, 5:gt_duration
    
    for i in range(len(gt)):
        sig, eig = gt[i, 0], gt[i, 1]
        for j in range(len(clusters)):
            sic, eic = clusters[j, 0], clusters[j, 1]
            
            if eig<sic:
                break                
            if sig>eic:
                continue
                
            acov[i, 0] = acov[i, 0] + 1
            
            if acov[i, 0]==1:
                acov[i, 1] = sic - sig #start error            
            
            acov[i, 2] = eic - eig #end error            
            
            if acov[i, 0]>1:
                acov[i, 3] += sic - clusters[j-1, 1] #gap error
            
            #acov[i, 4] = acov[i, 4] + (eic - sic+1) #cover_duration
            #acov[i, 5] = eig - sig+1 #gt_duration
            
     
    clcov = np.zeros((len(clusters), 3))
    # 0:coverd_by_type
    for j in range(len(clusters)):
        sic, eic = clusters[j, 0], clusters[j, 1]
        for i in range(len(gt)):
            sig, eig, mt = gt[i, 0], gt[i, 1], gt[i, 2]
            
            if eic<sig:
                break                
            if sic>eig:
                continue
                
            clcov[j, mt-1] = 1
            
        
    return gt, np.array(acov), np.array(clcov)
        


# In[1]:


def get_metric_results(gts, acovs, clcovs):
    cond = (gts[:, 2]<=2)
    gts = gts[cond]
    acovs = acovs[cond]

    res ={}

    res['total'] = acovs.shape[0]
    res['total_meal'] = np.sum(gts[:,2]==1)
    res['total_snack'] = np.sum(gts[:,2]==2)
    
    #print(res['total'], res['total_meal'], res['total_snack'])
    
    res['tp'] = np.sum( (acovs[:, 0]>=1) )
    res['tp_meal'] = np.sum( (acovs[:, 0]>=1) & (gts[:,2]==1) )
    res['tp_snack'] = np.sum( (acovs[:, 0]>=1) & (gts[:,2]==2) )
    
    res['recall'] = res['tp']/res['total']
    res['recall_meal'] = res['tp_meal']/res['total_meal']
    res['recall_snack'] = res['tp_snack']/res['total_snack']
    
    res['fp'] = np.sum((clcovs[:, 0]==0) & (clcovs[:, 1]==0))
    res['precision'] = res['tp']/(res['tp'] + res['fp'])
    res['f1'] = 2*res['precision']*res['recall']/(res['precision']+res['recall'])
    
    res['start_error'] = np.sum(np.abs(acovs[:, 1]))/res['total']/16
    res['start_error_meal'] = np.sum(np.abs(acovs[gts[:,2]==1, 1]))/res['total_meal']/16
    res['start_error_snack'] = np.sum(np.abs(acovs[gts[:,2]==2, 1]))/res['total_snack']/16
    
    res['end_error'] = np.sum(np.abs(acovs[:, 2]))/res['total']/16
    res['end_error_meal'] = np.sum(np.abs(acovs[gts[:,2]==1, 2]))/res['total_meal']/16
    res['end_error_snack'] = np.sum(np.abs(acovs[gts[:,2]==2, 2]))/res['total_snack']/16
    
    res['fragment_error'] = np.sum( (acovs[:, 0]>1) )/res['total']
    res['fragment_error_meal'] = np.sum( (acovs[:, 0]>1) & (gts[:,2]==1) )/res['total_meal']
    res['fragment_error_snack'] = np.sum( (acovs[:, 0]>1) & (gts[:,2]==2) )/res['total_snack']
    
    return res

