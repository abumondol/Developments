
# coding: utf-8

# In[24]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import copy
import meal_detection_utils as mdu
import result_utils as resu


# In[25]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[26]:


annots = mfileu.read_file('data', 'free_annots_steven_processed.pkl')
peak_ssilp = mfileu.read_file('classification_results', 'free_ssilp_cnn_peaks.pkl')
subj_count = len(annots)


# In[27]:


def get_subject_result(ssilp, subj, proba, max_bite_distance, min_bite_count):
    gts, acovs, clcovs = [], [], []
    
    for sess in range(len(annots[subj])):            
        a = annots[subj][sess]            
        ix = ssilp[subj][sess][:, 2]
        p = ssilp[subj][sess][:, -1]            
        bites = ix[p>=proba]

        episodes = mdu.cluster_bites_by_max_distance(bites, max_bite_distance, min_bite_count)

        gt, ac, clc = resu.get_meal_detection_results_free(a, clusters=episodes)            

        #gt = mdpu.add_subj_sess_to_array(gt, subj, sess, at_begin=False)
        #ac = mdpu.add_subj_sess_to_array(ac, subj, sess, at_begin=False)
        #clc = mdpu.add_subj_sess_to_array(clc, subj, sess, at_begin=False)

        gts = gt if len(gts)==0 else np.concatenate((gts, gt))
        acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
        clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
    
    return gts, acovs, clcovs


# In[28]:


def get_results_exclude_subj(ssilp, exclude_subj, proba, max_bite_distance, min_bite_count):    
    gts, acovs, clcovs = [], [], []
    
    for subj in range(len(annots)):
        if subj == exclude_subj:
            continue
            
        gt, ac, clc = get_subject_result(ssilp, subj, proba, max_bite_distance, min_bite_count)
        
        gts = gt if len(gts)==0 else np.concatenate((gts, gt))
        acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
        clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))    

    res = resu.get_metric_results_free(gts, acovs, clcovs)
    return res 


# In[29]:


def get_results_all_subj(ssilp, params):    
    gts, acovs, clcovs = [], [], []
    
    for subj in range(len(annots)):
        proba, max_bite_distance, min_bite_count = params[subj, 0], params[subj, 1], params[subj, 2]
        
        gt, ac, clc = get_subject_result(ssilp, subj, proba, max_bite_distance, min_bite_count)
        
        gts = gt if len(gts)==0 else np.concatenate((gts, gt))
        acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
        clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))  
    
    print(clcovs.shape)
    res = resu.get_metric_results_free(gts, acovs, clcovs)
    return res 


# In[40]:


res_all = []
for subj in range(subj_count):
    print(subj, end=" | ")
    for proba in np.arange(0.1, 0.9, 0.1):
        for max_bite_distance in range(30, 200, 10):
            for min_bite_count in range(2, 7):                
                res  = get_results_exclude_subj(peak_ssilp, exclude_subj=subj, proba=proba, max_bite_distance=16*max_bite_distance, min_bite_count=min_bite_count)
                res_all.append([subj, proba, max_bite_distance, min_bite_count, res["f1"]])
                


# In[41]:


res = np.array(res_all)
params = np.zeros((subj_count, 3))

for subj in range(subj_count):
    x = res[res[:, 0]==subj, :]
    f1 = x[:, -1]
    max_f1 = np.amax(f1)    
    
    x = x[x[:, -1]==max_f1, :]    
    for i in range(len(x)):
        print("{:2d} :: {:.2f}, {:.0f}, {:.0f}, {:.5f}".format(subj, x[i, 1], x[i, 2], x[i, 3], x[i, 4]))
    
    print()
    
    proba, max_bite_distance, min_bite_count = x[0, 1], x[0, 2]*16, x[0, 3]
    params[subj, :] = [proba, max_bite_distance, min_bite_count]


# In[42]:


params


# In[43]:


#res = get_results_exclude_subj(peak_ssilp, -1, proba=0.02, max_bite_distance=16*100, min_bite_count=4)
res = get_results_all_subj(peak_ssilp, params)


# In[44]:


for key, val in res.items():
    print("{} : {}".format(key, val))

