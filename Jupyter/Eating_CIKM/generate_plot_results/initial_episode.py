
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import copy
import meal_detection_utils as mdu
import result_utils as resu

import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[2]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[3]:


lab_free= 'lab'
assert lab_free in ['lab', 'free']

if lab_free=='lab':    
    episodes = mfileu.read_file('data', 'lab_episodes_steven_right.pkl')
    annots = mfileu.read_file('data', 'lab_annots_steven_right.pkl')
    print("Lab episode count: ", len(episodes))
else:    
    annots = mfileu.read_file('data', 'free_annots_steven_processed.pkl')
    subj_count = len(annots)


# In[4]:


minDuration=0
minCount=3
mergeDistance=5*60*16


# In[5]:


def get_results_exclude_subj(bites, percentile_proba, exclude_subj):    
    gts, acovs, clcovs = [], [], []
    pos_bite_detected, neg_bite_detected = 0, 0
    
    for subj in range(len(annots)):
        if subj == exclude_subj:
            continue
        
        for sess in range(len(annots[subj])):
            if lab_free=='lab':
                cond = (episodes[:,0]==subj) & (episodes[:,1]==sess)
                a = episodes[cond, 2:]
            else:
                a = annots[subj][sess]
            
            
            b = bites[(subj, sess, percentile_proba)]
            
            clusters1 = mdu.cluster_bites_by_minute(b)
            clusters2 = mdu.filter_clusters(clusters1, minDuration, minCount)            
            clusters = mdu.merge_clusters(clusters2, mergeDistance)
            #print(len(bites), len(clusters1), len(clusters2), len(clusters))
            
            gt, ac, clc = resu.get_meal_detection_results_lab(a, clusters=clusters)
            assert len(gt) == len(ac)
            
            ########### Bite Count            
            #pb, nb = get_bite_count_sess(clusters, clc)            
            #pos_bite_detected += pb            
            #neg_bite_detected += nb
            
            #############
                        
            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
    
    if lab_free:
        res = resu.get_metric_results_lab(gts, acovs, clcovs)
    else:
        res = resu.get_metric_results_free(gts, acovs, clcovs)
        
    #res["pos_bite_detected"] = pos_bite_detected    
    #res["neg_bite_detected"] = neg_bite_detected    
    return res 


# In[6]:


def get_subj_p(bites, fixed_p, pct_proba, rf_our):
    subj_p =[]
    for subj in range(len(annots)):
        if fixed_p:
            p = 0.5 if pct_proba=='proba' else 99.9
            subj_p.append([subj, p, 0])
            continue

        
        ps = range(10, 95, 5) if pct_proba=='proba' else range(9900,10000)
        best_f1, best_p = 0, 0
        for i in ps:
            p = i/100    
            r = get_results_exclude_subj(bites, p, exclude_subj = subj)
            if r["f1"]>best_f1:
                best_f1 = r["f1"]
                best_p = p

        subj_p.append([subj, best_p, best_f1])

    subj_p = np.array(subj_p)
    return subj_p


# In[7]:


def get_result(bites, subj_p):
    gts, acovs, clcovs = [], [], []
    pos_bite_detected, neg_bite_detected = 0, 0

    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            if lab_free=='lab':
                cond = (episodes[:,0]==subj) & (episodes[:,1]==sess)
                a = episodes[cond, 2:]
            else:
                a = annots[subj][sess]

            p = subj_p[subj, 1]
            b = bites[(subj, sess, p)]

            clusters1 = mdu.cluster_bites_by_minute(b)
            clusters2 = mdu.filter_clusters(clusters1, minDuration, minCount)            
            clusters = mdu.merge_clusters(clusters2, mergeDistance)
            #print(len(bites), len(clusters1), len(clusters2), len(clusters))

            gt, ac, clc = resu.get_meal_detection_results_lab(a, clusters=clusters)
            assert len(gt) == len(ac)

            ########### Bite Count            
            #pb, nb = get_bite_count_sess(clusters, clc)            
            #pos_bite_detected += pb            
            #neg_bite_detected += nb

            #############

            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))

    if lab_free:
        res = resu.get_metric_results_lab(gts, acovs, clcovs)
    else:
        res = resu.get_metric_results_free(gts, acovs, clcovs)
    #res["pos_bite_detected"] = pos_bite_detected    
    #res["neg_bite_detected"] = neg_bite_detected
    
    return res


# In[8]:


res_all={}
for pct_proba in ["pct_offline", "proba"]:
    res = np.zeros((4, 3))
    i = 0
    for rf_our in ["rf", "our"]:
        bites= mfileu.read_file('generated_for_result/bites', '{}_bites_{}_{}.pkl'.format(lab_free, pct_proba, rf_our))
        for fixed_p in [True, False]:
            print(pct_proba, rf_our, fixed_p)            
            subj_p = get_subj_p(bites, fixed_p, pct_proba, rf_our)
            print(subj_p)
            res = get_result(bites, subj_p)
            p, r, f = res["percentile"], res["recall"], res["recall"]
            res[i, :] = [p, r, f]
            i += 1
            
    res_all[pct_proba] = res    
    print(res)

