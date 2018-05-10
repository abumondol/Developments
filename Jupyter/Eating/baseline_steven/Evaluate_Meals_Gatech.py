
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_steven_free_utils as msfreeu
import my_classification_utils as mcu
import my_meal_detection_utils as mmdu
#importlib.reload(mdu)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
#lab_data = mfileu.read_file('data', 'lab_data_steven.pkl')
#free_data = mfileu.read_file('data', 'free_data_steven_right.pkl')


# In[4]:


def find_meal_result(gt, clusters):
    acov = np.zeros((len(gt), 4))         
    # 0:coverd_by_count, 1:start_error, 2:end_error, 3:gap_error, 4:cover_duration, 5:gt_duration
    
    for i in range(len(gt)):
        sig, eig = gt[i, 0], gt[i, 1]
        for j in range(len(cl)):
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
                acov[i, 3] += sic - cl[j-1, 1] #gap error
            
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
            
        
    return acov, clcov
        


# In[5]:


def find_events_by_th(proba, indices, proba_th):
    count = len(proba)
    
    #print("Count total, Threshold, count greater proba_th: ", count, proba_th, np.sum(proba>proba_th))    
    
    res =[]
    inside = False
    for i in range(count):
        if proba[i]>=proba_th and inside==False:
            inside = True
            si = indices[i]
        elif proba[i]<proba_th/2 and inside==True:            
            res.append([si, indices[i-1]])
            inside = False
        
    res = np.array(res).astype(int)    
    #res = np.mean(res, axis=1).astype(int)
    return res


# In[6]:


def cluster_events_by_minute(meal_indices, bite_indices):        
    res = []
    m = np.zeros((len(meal_indices), 1)) + 1
    mix = np.concatenate((meal_indices, m), axis=1)
    
    b = np.zeros((len(bite_indices), 1)) + 2
    bix = np.concatenate((bite_indices, b), axis=1)
    
    indices = np.concatenate((mix, bix))
    ix = np.argsort(indices[:, 0])
    indices = indices[ix, :]
    indices = indices.astype(int)
    
    assert indices.shape[1]==3
    assert len(indices) == len(meal_indices)+len(bite_indices)
    for i in range(1, len(indices)):
        assert indices[i,0]>=indices[i-1, 0]
        
    count = len(indices)
    clusters = []
    j = 0
    for i in range(1, count):
        if  indices[i, 0] - indices[i-1, 1]>16*60:
            si = indices[j,0]
            ei = indices[i-1,1]
            mc = np.sum(indices[j:i, 2]==1)
            bc = np.sum(indices[j:i, 2]==2)
            clusters.append([si, ei, mc, bc])
            j = i
    
    si = indices[j,0]
    ei = indices[count-1,1]
    mc = np.sum(indices[j:count, 2]==1)
    bc = np.sum(indices[j:count, 2]==2)
    clusters.append([si, ei, mc, bc])
    clusters = np.array(clusters).astype(int)
    
    print(np.sum(clusters[:, 2]), len(meal_indices))
    print(np.sum(clusters[:, 3]), len(bite_indices))    
    assert np.sum(clusters[:, 2]) == len(meal_indices)
    assert np.sum(clusters[:, 3]) == len(bite_indices)
    
    return clusters


# In[7]:


def cluster_dbscan(bites, minPts, eps):    
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
    
    clusters = np.array(clusters).astype(int)    
    res = np.zeros(clusters.shape).astype(int)    
    for i in range(clusters.shape[0]):
        res[i, 0] = bites[clusters[i, 0]]
        res[i, 1] = bites[clusters[i, 1]]
        
    return res


# In[8]:


def cluster_dbscan_eat(events, minPts, eps):
    durations = events[:,1]-events[:,0]+1
    core_pt1 = (durations>=minPts) & (events[:, -1]!=0)
    core_pt2 = (durations>=16*60*5)
    core_pt = core_pt1 | core_pt2
    
    clusters = []
    
    si, last_cpi  = -1, -1        
    for i in range(0, len(events)):        
        if last_cpi < 0:            
            if core_pt[i]:
                last_cpi = i
                si = i
                while si>=0 and (events[i, 0] - events[si, 1])<=eps:
                    si -= 1
                si +=1
                
            continue
            
            
        if events[i, 0] - events[last_cpi, 1] > eps:
            if si>=0 and ( (not core_pt[i]) or (events[i, 0] - events[i-1, 1] > eps) ):
                clusters.append([si, i-1])                
                si = -1
                
            
            if core_pt[i] and si<0:                
                si = i 
                while si>=0 and (events[i, 0] - events[si, 1])<=eps:
                    si -= 1
                          
                si +=1
        
        if core_pt[i]:
            last_cpi = i    
    
    if si>=0:
        clusters.append([si, len(events)-1])                
    
    clusters = np.array(clusters).astype(int)    
    res = np.zeros(clusters.shape).astype(int)    
    for i in range(clusters.shape[0]):
        res[i, 0] = events[clusters[i, 0], 0]
        res[i, 1] = events[clusters[i, 1], 1]
        
    return res


# In[9]:


all_proba_bite = []
all_proba_free = []
for subj in range(len(annots)):
    subj_proba_bite = []
    subj_proba_free = []
    for sess in range(len(annots[subj])):
        r = mfileu.read_file('baseline_test_proba', 'bite_free_'+str(subj)+'_'+str(sess)+".pkl")
        subj_proba_bite.append(r)
        
        r = mfileu.read_file('baseline_test_proba', 'meal_free_'+str(subj)+'_'+str(sess)+".pkl")
        subj_proba_free.append(r)
        
    all_proba_bite.append(subj_proba_bite)
    all_proba_free.append(subj_proba_free)


# In[11]:


all_proba_bite_gatech = []
all_proba_bite_gatech_accel = []
for subj in range(len(annots)):
    subj_proba_bite, subj_proba_bite_accel = [], []    
    for sess in range(len(annots[subj])):
        r = mfileu.read_file('baseline_test_proba_gatech', 'bite_free_'+str(subj)+'_'+str(sess)+".pkl")
        subj_proba_bite.append(r)
        
        r = mfileu.read_file('baseline_test_proba_gatech', 'bite_free_'+str(subj)+'_'+str(sess)+"_accel.pkl")
        subj_proba_bite_accel.append(r)
        
    all_proba_bite_gatech.append(subj_proba_bite)    
    all_proba_bite_gatech_accel.append(subj_proba_bite_accel)    


# In[14]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

def get_result(subj, sess, proba_th_bite, proba_th_free, plot=False):
    results = []
    
    print("Subj, sess: ", subj, sess)

    a = annots[subj][sess]        
    a = msfreeu.process_anntos(len(free_data[subj][sess]), a)
    
    proba = all_proba_bite[subj][sess]
    indices = proba[:, 0]+40
    proba = proba[:, -1]    
    bite_indices = find_events(proba, indices, proba_th=proba_th_bite)        

    proba = all_proba_free[subj][sess]
    assert len(indices) == len(proba)
    indices = proba[:, 0]+40
    proba = proba[:, -1]        
    th = np.percentile(proba, 99.9)
    meal_indices = find_events(proba, indices, proba_th=proba_th_free)

    clusters = cluster_events_by_minute(meal_indices, bite_indices)
    #print("Orginal cluster shape: ", clusters.shape)
    #c = np.concatenate((clusters, (clusters[:, 1]-clusters[:,0]).reshape(-1, 1)), axis=1)
    #print(c)

    clusters = cluster_events_dbscan_eat(clusters, minPts=16*10, eps=5*16*60)
    print("New cluster count: ", clusters.shape)
    print(clusters)

    clusters2 = cluster_events_dbscan(np.mean(bite_indices, axis=1).astype(int), minPts=2, eps=16*60)
    print("Cluster2 count: ", clusters2.shape)
    print(clusters2)
    
    result = find_meal_result(a, clusters=clusters)
    
    ###############################################

    if not plot:
        return results

    idf = 16*60*60
    fig = plt.figure(figsize=(20,8))
    ax = plt.subplot(111)        
    ax.scatter(bite_indices[:,0]/idf, np.zeros((len(bite_indices)),)+1.5, marker='x', s=10, color='blue')                
    ax.scatter(meal_indices[:,0]/idf, np.zeros((len(meal_indices),))+2, marker='x', s=10, color='red')                

    clrs = ['', 'blue', 'green', 'red', 'black']
    for i in range(len(a)):
        si = a[i, 0]/idf
        ei = a[i, 1]/idf
        mt = a[i, 2]            
        ax.plot([si, ei], [0.5, 0.5], color=clrs[mt], linewidth=5)

    for i in range(len(clusters)):
        si = clusters[i, 0]/idf
        ei = clusters[i, 1]/idf            
        ax.plot([si, ei], [0.75, 0.75], color='black', linewidth=5)

    for i in range(len(clusters2)):
        si = clusters2[i, 0]/idf
        ei = clusters2[i, 1]/idf            
        ax.plot([si, ei], [1.0, 1.0], color='black', linewidth=5)


    plt.xlabel('Time (Hour)', fontsize=20)        
    plt.ylim([0, 3])
    plt.grid(True)
    plt.show()        

    return results


# In[24]:


def get_proba_th_other(all_proba, subj, exclude_sess):
    p = []
    for sess in range(len(annots[subj])):
        if sess != exclude_sess:
            p = all_proba[subj][sess] if len(p)==0 else np.concatenate((p, all_proba[subj][sess]))
            
    return np.percentile(p[:, -1], 99.9)
            

for subj in range(len(annots)):
    for sess in range(len(annots[subj])):
        
        proba = all_proba_bite[subj][sess]        
        proba_th_bite = np.percentile(proba[:, -1], 99.9)
        #proba_th_bite = get_proba_th_other(all_proba_bite, subj, sess)
        
        proba = all_proba_free[subj][sess]
        proba_th_free = np.percentile(proba[:, -1], 99.9)
        #proba_th_free = get_proba_th_other(all_proba_free, subj, sess)
        
        get_result(subj, sess, proba_th_bite=proba_th_bite, proba_th_free= proba_th_free, plot=True)
        
        

