
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import matplotlib.pyplot as plt
import bite_detection_utils as bdu


# In[2]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    lab_data = pickle.load(file)

with open('C:/ASM/DevData/eating/data/steven_free_data.pkl', 'rb') as file:
    free_data = pickle.load(file)


# #interval analysis
# diffs = np.empty((0,))
# for subject in range(len(lab_data)):
#     for sess in range(len(lab_data[subject])):
#         a = lab_data[subject][sess]['annots']
#         df = a[1:,0] - a[:-1, 0]
#         diffs = np.concatenate((diffs, df))
# 
# diffs /=16
# 
# print(len(diffs))
# diffs = diffs[diffs<=10]
# #print(len(diffs))
# #diffs = diffs[diffs<2]
# #print(len(diffs))
# 
# plt.hist(diffs, bins=50)
# plt.show()

# In[22]:


def get_stats_lab(x_th, var_th, min_bite_interval=2*16, window_size=6*16, print_flag=False):
    
    neg_label_count, pos_label_count, bite_label_count, sip_label_count = 0, 0, 0, 0
    annot_count, bite_count, sip_count, miss_count = 0, 0, 0, 0
    
    for subject in range(len(lab_data)):
        for sess in range(len(lab_data[subject])):
            data = lab_data[subject][sess]['data']
            annots = lab_data[subject][sess]['annots']
            
            mps0 = bdu.find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)        
            mps = bdu.remove_min_points_at_boundary(mps0, len(data), window_size)        
            
            w = bdu.get_windows(data[:, 1:], mps, window_size)
            v = bdu.get_variance(w, 3, 5)
            mps = mps[v>=var_th]
            labels, annot_covered = bdu.get_labels_lab(mps, annots, window_size)
            
            neg_label_count += np.sum(labels==0)
            pos_label_count += np.sum(labels>0)
            bite_label_count += np.sum(labels==1)
            sip_label_count += np.sum(labels==2)
            
            
            a = annots[:, 1]
            ac = len(a)
            bc = np.sum((a==1) & (annot_covered>0))
            sc = np.sum((a==2) & (annot_covered>0))
            mc = np.sum(annot_covered==0)            
            
            annot_count += ac
            bite_count +=bc
            sip_count +=sc
            miss_count += mc
            
            res_label = [neg_label_count, pos_label_count, bite_label_count, sip_label_count]
            res_annot = [0.001, annot_count, bite_count, sip_count, miss_count]
            
    return res_annot, res_label
            


# In[23]:


def get_stats_free(x_th, var_th, min_bite_interval=2*16, window_size=6*16, print_flag=False):
    
    neg_label_count, pos_label_count, bite_label_count, snack_label_count, sip_label_count = 0, 0, 0, 0, 0    
    
    for subject in range(len(free_data)):
        for sess in range(len(free_data[subject])):
            data = free_data[subject][sess]['data']
            annots = free_data[subject][sess]['annots']
            
            mps0 = bdu.find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)        
            mps = bdu.remove_min_points_at_boundary(mps0, len(data), window_size)        
            
            w = bdu.get_windows(data[:, 1:], mps, window_size)
            v = bdu.get_variance(w, 3, 5)
            mps = mps[v>=var_th]
            labels = bdu.get_labels_free(mps, annots, window_size)
            
            neg_label_count += np.sum(labels==0)
            pos_label_count += np.sum(labels>0)
            bite_label_count += np.sum(labels==1)
            snack_label_count += np.sum(labels==2)
            sip_label_count += np.sum(labels==3)
            
    return [neg_label_count, pos_label_count, bite_label_count, snack_label_count, sip_label_count]
            


# In[24]:


xvs = []
annot_lab = []
label_lab = []
res_free = []

for x_th in [0, -0.1, -0.2, -0.3, -0.4]:
    for var_th in [0, 0.25, 0.5, 0.75, 1]:             
        
        ra_lab, rl_lab = get_stats_lab(x_th, var_th)
        f_lab = get_stats_free(x_th, var_th)         
        
        print("\nx_th, var_th: ", x_th, var_th)
        print("   Lab Annot >> ", ra_lab)
        print("   Lab Label >> ", rl_lab)
        print("   Free      >> ", f_lab)
        
        xvs.append([x_th, var_th])
        annot_lab.append(ra_lab)
        label_lab.append(rl_lab)
        res_free.append(f_lab)        

