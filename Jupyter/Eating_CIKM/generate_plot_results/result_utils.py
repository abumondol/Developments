
# coding: utf-8

# In[1]:


import numpy as np


# In[3]:


def get_meal_detection_results_lab(gt, clusters):
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
            
     
    clcov = np.zeros((len(clusters), 1))    
    for j in range(len(clusters)):
        sic, eic = clusters[j, 0], clusters[j, 1]
        for i in range(len(gt)):
            sig, eig = gt[i, 0], gt[i, 1]
            
            if eic<sig:
                break                
            if sic>eig:
                continue
                
            clcov[j, 0] = 1
            
        
    return gt, acov, clcov
        


# In[ ]:


def get_meal_detection_results_free(gt, clusters):
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
            
        
    return gt, acov, clcov
        


# In[2]:


def get_metric_results_lab(gts, acovs, clcovs):
    #cond = (gts[:, 2]<=2)
    #gts = gts[cond]    
    #acovs = acovs[cond]
    
    res ={}
    
    res['total'] = acovs.shape[0]
    res['tp'] = np.sum( (acovs[:, 0]>=1) )
    res['fp'] = np.sum(clcovs[:, 0]==0) if len(clcovs>0) else 0
                       
    res['recall'] = res['tp']/res['total'] if res['total']>0 else 0
    res['precision'] = res['tp']/(res['tp'] + res['fp']) if (res['tp'] + res['fp'])>0 else 0
    res['f1'] = 2*res['precision']*res['recall']/(res['precision']+res['recall']) if (res['precision']+res['recall'])>0 else 0
    
    res['start_error'] = np.sum(np.abs(acovs[:, 1]))/res['tp']/16 if res['tp']> 0 else 0
    res['end_error'] = np.sum(np.abs(acovs[:, 2]))/res['tp']/16 if res['tp']> 0 else 0   
    res['fragment_error'] = np.sum( (acovs[:, 0]>1) )/res['tp'] if res['tp']> 0 else 0
    
    return res


# In[ ]:


def get_metric_results_free(gts, acovs, clcovs):    
    
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
    res['recall_meal'] = res['tp_meal']/res['total_meal'] if res['total_meal']>0 else 0
    res['recall_snack'] = res['tp_snack']/res['total_snack'] if res['total_snack']>0 else 0
    
    res['fp'] = np.sum((clcovs[:, 0]==0) & (clcovs[:, 1]==0)) if len(clcovs>0) else 0
    res['precision'] = res['tp']/(res['tp'] + res['fp']) if (res['tp'] + res['fp'])>0 else 0
    res['f1'] = 2*res['precision']*res['recall']/(res['precision']+res['recall']) if (res['precision']+res['recall'])>0 else 0
    
    res['start_error'] = np.sum(np.abs(acovs[:, 1]))/res['total']/16
    res['start_error_meal'] = np.sum(np.abs(acovs[gts[:,2]==1, 1]))/res['total_meal']/16 if res['total_meal']>0 else 0
    res['start_error_snack'] = np.sum(np.abs(acovs[gts[:,2]==2, 1]))/res['total_snack']/16 if res['total_snack']>0 else 0
    
    res['end_error'] = np.sum(np.abs(acovs[:, 2]))/res['total']/16
    res['end_error_meal'] = np.sum(np.abs(acovs[gts[:,2]==1, 2]))/res['total_meal']/16 if res['total_snack']>0 else 0
    res['end_error_snack'] = np.sum(np.abs(acovs[gts[:,2]==2, 2]))/res['total_snack']/16 if res['total_snack']>0 else 0
    
    res['fragment_error'] = np.sum( (acovs[:, 0]>1) )/res['total']
    res['fragment_error_meal'] = np.sum( (acovs[:, 0]>1) & (gts[:,2]==1) )/res['total_meal'] if res['total_snack']>0 else 0
    res['fragment_error_snack'] = np.sum( (acovs[:, 0]>1) & (gts[:,2]==2) )/res['total_snack'] if res['total_snack']>0 else 0
    
    return res

