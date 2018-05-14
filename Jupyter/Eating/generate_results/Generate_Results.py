
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import generate_result_utils as gresu


# In[14]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_cluster_utils as mclusteru
import my_meal_detection_utils as mmdu
import my_data_process_utils as mdpu
import my_steven_free_utils as msfreeu
importlib.reload(mclusteru)


# In[15]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
ds = mfileu.read_file('data', 'free_data_steven_right.pkl')


# In[16]:


def find_meals(clusters, minDuration):
    if minDuration<=0 or len(cluster)==0:
        return clusters
    
    cond = ((clusters[:,1]-clusters[:, 0])>=minDuration)
    return clusters[cond]    


# In[17]:


def get_results(percentile_bites, minDuration=0,  mergeDistance=0, off_on="offline"):    
    gts, acovs, clcovs = [], [], []    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            a = annots[subj][sess]        
            a = msfreeu.process_anntos(len(ds[subj][sess]), a)
            
            bites = percentile_bites[(subj, sess)][off_on]
            clusters = mclusteru.cluster_bites_by_minute(bites)
            meals = find_meals(clusters, minDuration)
            if mergeDistance>0:
                meals = mclusteru.merge_clusters(meals, mergeDistance)
            
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


# In[20]:


all_bites_our = mfileu.read_file('final_results', 'all_bites_our_6s.pkl')
all_bites_steven = mfileu.read_file('final_results', 'all_bites_steven.pkl')


# In[ ]:


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

