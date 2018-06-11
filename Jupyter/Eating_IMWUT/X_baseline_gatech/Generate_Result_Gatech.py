
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
#implortlib.reload(mmdu)


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
    ax.scatter(bite_indices/idf, np.zeros((len(bite_indices)),)+1, marker='x', s=10, color='blue')                
    
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
    

    plt.title("Subj, sess:{}, {}".format(subj, sess))
    plt.xlabel('Time (Hour)', fontsize=20)        
    plt.ylim([0, 2])
    plt.grid(True)
    plt.show()        

    return result


# In[7]:


def get_metric_results(minPts, eps):
    gts, acovs, clcovs = [], [], []
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
                              
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


# In[11]:


res ={}
for minPts in range(1, 11):
    for eps in range(10, 601, 10):
        print(minPts, eps, end=' | ')
        res[(minPts, eps)] = get_metric_results(minPts, eps*16)

mfileu.write_file('final_results', 'gatech.pkl', res)

