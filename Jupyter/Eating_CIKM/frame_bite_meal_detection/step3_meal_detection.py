
# coding: utf-8

# In[1]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import copy


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
#importlib.reload(mclusteru)


# In[ ]:


all_bites = mfileu.read_file('generated_for_result', 'all_bites.pkl')


# In[ ]:


def get_meals(bites, minDuration=30*16, minCount=3,  mergeDistance=5*60*16):    
    gts, acovs, clcovs = [], [], []
    pos_bite_detected, neg_bite_detected = 0, 0
    
    for subj in range(len(annots)):
        for sess in range(len(annots[subj])):
            cond = (episodes[:,0]==subj) & (episodes[:,1]==sess)
            a = episodes[cond, 2:]
            
            bites = all_bites[subj][sess]            
            clusters1 = mclusteru.cluster_bites_by_minute(bites)
            clusters2 = mclusteru.filter_clusters(clusters1, minDuration, minCount)            
            clusters = mclusteru.merge_clusters(clusters2, mergeDistance)
            #print(len(bites), len(clusters1), len(clusters2), len(clusters))
            
            gt, ac, clc = mmdu.get_meal_detection_results_lab(a, clusters=clusters)
            assert len(gt) == len(ac)
            
            ########### Bite Count            
            pb, nb = get_bite_count_sess(clusters, clc)            
            pos_bite_detected += pb            
            neg_bite_detected += nb
            
            #############
                        
            gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
            ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
            clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
            
            
    res = mmdu.get_metric_results_lab(gts, acovs, clcovs)
    res["pos_bite_detected"] = pos_bite_detected    
    res["neg_bite_detected"] = neg_bite_detected
    
    return res 

