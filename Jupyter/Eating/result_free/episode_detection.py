
# coding: utf-8

# In[7]:


import numpy as np
import scipy as sp
import os 
import sys
import importlib
import copy
import meal_detection_utils as mdu
import result_utils as resu


# In[8]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_data_process_utils as mdpu


# In[9]:


annots = mfileu.read_file('data', 'free_annots_steven_processed.pkl')
bites_baseline = mfileu.read_file('bites', 'bites_baseline.pkl')
bites_baseline_our = mfileu.read_file('bites', 'bites_baseline_our.pkl')
subj_count = len(annots)


# In[10]:


def get_results(bites, exclude_subj, max_bite_distance, min_bite_count):
    gts, acovs, clcovs = [], [], []
    
    for subj in range(len(bites)):
        if subj==exclude_subj:
            continue
            
        for sess in range(len(bites[subj])):            
            a = annots[subj][sess]                        
            b = bites[subj][sess]

            episodes = mdu.cluster_bites_by_max_distance(b, max_bite_distance, min_bite_count)
            
            gt, ac, clc = resu.get_meal_detection_results_free(a, clusters=episodes)            
            
            gts = gt if len(gts)==0 else np.concatenate((gts, gt))
            acovs = ac if len(acovs)==0 else np.concatenate((acovs, ac))
            clcovs = clc if len(clcovs)==0 else np.concatenate((clcovs, clc))
            
    res = resu.get_metric_results_free(gts, acovs, clcovs)    
    return res


# In[11]:


max_res = []
for min_bite_count in range(2, 10):
    for max_bite_distance  in range(15, 100, 15):
        
        
        res = get_results(bites_baseline, exclude_subj=-1, max_bite_distance=max_bite_distance*16, min_bite_count=min_bite_count)
        
        if len(max_res)== 0 or res["f1"]>max_res["f1"]:
            max_res = res
            max_res['mbp']=min_bite_count
            max_res['mbd']=max_bite_distance
        
        #print(min_bite_count, max_bite_distance , res["precision"], res["recall"], res["f1"])
    


# In[12]:


for key, val in max_res.items():
    print("{} : {}".format(key, val))

