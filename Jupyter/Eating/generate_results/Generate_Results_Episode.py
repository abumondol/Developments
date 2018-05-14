
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import generate_result_utils as gresu


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_cluster_utils as mclusteru
import my_meal_detection_utils as mmdu
import my_data_process_utils as mdpu
import my_steven_free_utils as msfreeu
importlib.reload(mclusteru)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
ds = mfileu.read_file('data', 'free_data_steven_right.pkl')


# In[4]:


all_bites_our = mfileu.read_file('final_results', 'all_bites_our_6s.pkl')
all_bites_steven = mfileu.read_file('final_results', 'all_bites_steven.pkl')


# In[18]:


def find_meals(clusters, minDuration):
    if minDuration<=0 or len(clusters)==0:
        return clusters
    
    cond = ((clusters[:,1]-clusters[:, 0])>=minDuration)
    return clusters[cond]    


# In[25]:


def get_results(percentile_bites, minDuration=0,  mergeDistance=0, off_on="offline"):    
    gts, acovs, clcovs = [], [], []    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            a = annots[subj][sess]        
            a = msfreeu.process_anntos(len(ds[subj][sess]), a)
            
            bites = percentile_bites[(subj, sess)][off_on]
            clusters = mclusteru.cluster_bites_by_minute(bites)
            clusters = find_meals(clusters, minDuration)
            if mergeDistance>0:
                clusters = mclusteru.merge_clusters(meals, mergeDistance)
            
            gt, ac, clc = mmdu.get_meal_detection_results(a, clusters=clusters)
            assert len(gt) == len(ac)
            
            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
    res = mmdu.get_metric_results(gts, acovs, clcovs)
    return res


# In[26]:


off_on = "offline"
percentile = 99.9    
print(percentile)
res = get_results(all_bites_steven[percentile], off_on=off_on)
for key, val in res.items():
    print(key, " : ", val)

print()
res = get_results(all_bites_our[percentile], off_on=off_on)
for key, val in res.items():
    print(key, " : ", val)


# In[29]:


import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')

def plot_result(metric, ylabel):
    fig = plt.figure(figsize=(10,5))
    ax = plt.subplot(111)  
    
    percentile = 99.9    
    minDurations = list(range(0, 300, 10))  
    
    r = np.zeros((len(minDurations), 5))
    for i in range(len(minDurations)):
        ro1 = get_results(all_bites_our[percentile], minDuration=minDurations[i], off_on="offline")
        ro2 = get_results(all_bites_our[percentile], minDuration=minDurations[i], off_on="online")
        rs1 = get_results(all_bites_steven[percentile], minDuration=minDurations[i], off_on="offline")
        rs2 = get_results(all_bites_steven[percentile], minDuration=minDurations[i], off_on="online")
        
        r[i, 0] = minDurations[i]
        r[i, 1] = ro1[metric]
        r[i, 2] = ro2[metric]
        r[i, 3] = rs1[metric]
        r[i, 4] = rs2[metric]
        

    ax.plot(r[:,0], r[:, 1], label='Our (Offline)', color='red')
    ax.plot(r[:,0], r[:, 2], label='Our (Online)', color='blue')
    ax.plot(r[:,0], r[:, 3], label='Steven (Offline)', color='red', linestyle='--')
    ax.plot(r[:,0], r[:, 4], label='Steven (Online)', color='blue', linestyle='--')

    
    plt.title(metric, fontsize=20)
    plt.xlabel("Min Meal Duration", fontsize=16)        
    plt.ylabel(ylabel, fontsize=16)            
    plt.legend()
    plt.grid(True)
    plt.show()       
        


# In[30]:


metrics = ['recall_meal', 'recall_snack', 'recall', 'precision', 'f1', 
           'start_error', 'start_error_meal', 'start_error_snack', 
           'end_error', 'end_error_meal', 'end_error_snack',
           'fragment_error', 'fragment_error_meal', 'fragment_error_snack']

for m in metrics:    
    plot_result(m, " ")

