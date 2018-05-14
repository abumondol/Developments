
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
import my_steven_free_utils as msfreeu
import my_cluster_utils as mclusteru
#importlib.reload(mdu)


# In[3]:


annots = mfileu.read_file('data', 'free_data_steven_annots.pkl')
ds = mfileu.read_file('data', 'free_data_steven_right.pkl')
fs = mfileu.read_file('features', 'free_features_steven_right.pkl')


# In[4]:


all_proba_steven = gresu.get_all_proba(annots, 'baseline_test_proba_bite_steven', 'bite_free', sess_avail=True)
pct_proba_steven = gresu.get_percentile_proba_all(all_proba_steven, fs)


# In[5]:


all_proba_our = gresu.get_all_proba(annots, 'our_test_proba_bite_6s', 'bite_free', sess_avail=True)
pct_proba_our = gresu.get_percentile_proba_all(all_proba_our, fs)


# In[16]:


def get_bites(subj, sess, percentile, isSteven):
    if isSteven:
        proba = all_proba_steven[subj][sess]
        indices = proba[:, 0] + 40        
        pct_proba = pct_proba_steven
    else:
        proba = all_proba_our[subj][sess]
        indices = proba[:, -2] + 48
        pct_proba = pct_proba_our
        
    proba = proba[:, -1]
    
    cond  = (pct_proba[:, 0]==subj) & (pct_proba[:, 1]==sess) & (pct_proba[:, 2]==percentile)
    assert np.sum(cond)==1    
    proba_th_offline, proba_th_online = pct_proba[cond, -2], pct_proba[cond, -1]    
    
    bites_offline = gresu.detect_gestures(proba, indices=indices, proba_th=proba_th_offline)
    bites_online = gresu.detect_gestures(proba, indices=indices, proba_th=proba_th_online)
    
    return bites_offline, bites_online


# In[17]:


def get_meal_result(subj, sess, percentile, minDuration, mergeDistance=0, plot=False):
    print_out, sys.stdout = sys.stdout, open(os.devnull, 'w')
    print("Subj, Sess:", subj, sess)
    
    results = []
    
    a = annots[subj][sess]        
    a = msfreeu.process_anntos(len(free_data[subj][sess]), a)
    
    proba = all_proba_bite[subj][sess]
    proba = proba[proba[:, 1]==sess, 2:]
    
    indices = proba[:, 0] + 48
    proba = proba[:, -1]
    proba_th = 0.75 #np.percentile(proba, 99.9)
    bite_indices = find_events(proba, indices, proba_th=proba_th)
    print(bite_indices)
    
    clusters = mclusteru.cluster_bites_by_minute(bite_indices)
    print(clusters)
    clusters = mclusteru.find_meals(clusters, minCount=minCount, mergeDistance=mergeDistance)
    print(clusters)
    
    cond = (clusters[:,1]-clusters[:, 0])>=1*16*60
    clusters = clusters[cond]
    result = mmdu.get_meal_detection_results(a, clusters=clusters)
    
    sys.stdout = print_out    


# In[18]:


def get_metric_results(minCount, mergeDistance, plotFlag=False):
    gts, acovs, clcovs = [], [], []
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            
            gt, ac, clc = get_result(subj, sess, minCount=minCount, mergeDistance=mergeDistance, plot=plotFlag)
            assert len(gt) == len(ac)

            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
    res = mmdu.get_metric_results(gts, acovs, clcovs)
    return res


# In[19]:


def get_bites_for_percentile(percentile, isSteven):    
    res ={}    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):            
            bites_offline, bites_online = get_bites(subj, sess, percentile, isSteven)
            res[(subj, sess)] = {"offline":bites_offline, "online":bites_online}            
    return res


# In[23]:


res ={}
isSteven = True 
for p in range(9980, 10000):
    percentile = p/100    
    print(percentile, isSteven)
    res[percentile] = get_bites_for_percentile(percentile, isSteven)
    
mfileu.write_file('final_results', 'all_bites_steven.pkl', res)


# In[ ]:


#for key, val in res[(60, 300)].items():
#    print(key, " : ", val)

