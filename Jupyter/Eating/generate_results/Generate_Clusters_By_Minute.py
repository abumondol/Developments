
# coding: utf-8

# In[4]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import generate_result_utils as gresu


# In[16]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_cluster_utils as mclusteru
importlib.reload(mclusteru)


# In[17]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')


# In[ ]:


def find_meals(clusters, minDuration):
    if minDuration<=0 or len(cluster)==0:
        return clusters
    
    cond = ((clusters[:,1]-clusters[:, 0])>=minDuration)
    return clusters[cond]    


# In[18]:


def get_results(percentile_bites, minDuration, mergeDistance=0):    
    res_offline, res_online = [], []
    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            bites = percentile_bites[(subj, sess)]["offline"]
            clusters = mclusteru.cluster_bites_by_minute(bites)
            meals = find_meals(clusters, minDuration)
            if mergeDistance>0:
                meals = mclusteru.merge_clusters(meals, mergeDistance)
            
            
            meals_offline.append() = meals
            
            bites = percentile_bites[(subj, sess)]["online"]
            clusters = mclusteru.cluster_bites_by_minute(bites)
            meals = find_meals(clusters, minDuration)
            if mergeDistance>0:
                meals = mclusteru.merge_clusters(meals, mergeDistance)
            meals_online = meals
            
            res[(subj, sess)] = {"offline":clusters_offline, "online":clusters_online}            
    return res


# In[19]:



all_bites = mfileu.read_file('final_results', 'all_bites_our_6s.pkl')
percentile = 99.9    
print(percentile)
res_offline, res_online = get_clusters(all_bites[percentile])


