
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
import my_data_process_utils as mdpu


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
#lab_data = mfileu.read_file('data', 'lab_data_steven.pkl')
free_data = mfileu.read_file('data', 'free_data_steven_right.pkl')


# In[4]:


all_proba_bite = []
for subj in range(len(annots)):
    subj_proba_bite = []
    for sess in range(len(annots[subj])):
        r1 = mfileu.read_file('baseline_test_proba_gatech', 'bite_free_'+str(subj)+'_'+str(sess)+".pkl")
        subj_proba_bite.append(r1)
        
    all_proba_bite.append(subj_proba_bite)        


# In[5]:


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


# In[6]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

def get_result(subj, sess, minPts, eps, plot=False):
    result = []
    
    #print("Subj, sess: ", subj, sess)

    a = annots[subj][sess]        
    a = msfreeu.process_anntos(len(free_data[subj][sess]), a)
    #print("Meal, snack count:", np.sum(a[:,2]==1), np.sum(a[:,2]==2))
    
    proba = all_proba_bite[subj][sess]
    indices = proba[:, 0]+48
    proba = proba[:, -1]    
    bite_indices = indices[proba>=0.5]    
    clusters = cluster_dbscan(bite_indices, minPts=minPts, eps=eps)
    
    result = mmdu.get_meal_detection_results(a, clusters=clusters)
    
    ###############################################
    if not plot:
        return result

    idf = 16*60*60
    fig = plt.figure(figsize=(20,8))
    ax = plt.subplot(111)        
    ax.scatter(bite_indices/idf, np.zeros((len(bite_indices)),)+1.5, marker='x', s=10, color='blue')                
    ax.scatter(bite_indices2/idf, np.zeros((len(bite_indices2),))+2, marker='x', s=10, color='red')                

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

    plt.title("Subj, sess:{}, {}".format(subj, sess))
    plt.xlabel('Time (Hour)', fontsize=20)        
    plt.ylim([0, 3])
    plt.grid(True)
    plt.show()        

    return result


# In[7]:


def get_metric_results(minPts, eps):
    gts, acovs, clcovs = [], [], []
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            
            proba = all_proba_bite[subj][sess]        
            gt, ac, clc = get_result(subj, sess, minPts=minPts, eps=eps, plot=False)
            assert len(gt) == len(ac)

            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
    res = mmdu.get_metric_results(gts, acovs, clcovs)
    return res


# In[8]:


importlib.reload(mmdu)


# In[11]:


res ={}
for minPts in range(1, 11):
    for eps in range(10, 601, 10):
        print(minPts, eps, end=' | ')
        res[(minPts, eps)] = get_metric_results(minPts, eps*16)


# In[12]:


mfileu.write_file('final_results', 'gatech.pkl', res)

