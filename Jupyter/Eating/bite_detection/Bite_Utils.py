
# coding: utf-8

# In[2]:


import numpy as np
import os
import sys
import importlib


# In[3]:


util_path = 'C:/ASM/Dropbox/Developments/Jupyter/Eating/myutils' if 'C:' in os.getcwd() else './myutils'
sys.path.append(util_path)
import my_file_utils as mfileu
import my_feature_utils as mfeatu
import my_segmentation_utils as msegu
import my_feature_utils as mfeatu
#importlib.reload()


# In[3]:


def get_labels_lab(mps, annots, max_distance, buffer_distance):           
    mp_count, annot_count = len(mps), len(annots)    
    if mp_count == 0:
        return mps
    
    labels = np.zeros((mp_count, 2))
    annot_covered = np.zeros((annot_count, ))
    
    if annot_count==0:
        return labels, annot_covered
    
    for i in range(annot_count):
        si, ei, label, repeat = annots[i, 0], annots[i, 1], annots[i, 2], annots[i, -1]
        cond = (mps>=si-buffer_distance) & (mps<=ei+buffer_distance)        
        labels[cond, 0] = -1        
        labels[cond, 1] = repeat
        
    for i in range(annot_count):
        si, ei, label, repeat = annots[i, 0], annots[i, 1], annots[i, 2], annots[i, -1]
        cond = (mps>=si-max_distance) & (mps<=ei+max_distance)        
        labels[cond, 0] = label
        labels[cond, 1] = repeat
        annot_covered[i] = np.sum(cond)    
    
    return labels.astype(int), annot_covered.astype(int)


# In[4]:


def print_label_summary(mps, acs): #mps: min_points_with_labels, acs: annot_covers
    print("Annot counts total, bite, drink, bite_ambigious, drink_ambigous: {}, {}, {}, {}, {}".format(len(acs), 
                                                                                                      np.sum(acs[:,-2]==1),
                                                                                                      np.sum(acs[:,-2]==2),
                                                                                                      np.sum((acs[:,-2]==1) &(acs[:,-1]>0)),
                                                                                                      np.sum((acs[:,-2]==2) &(acs[:,-1]>0))))
    acs = acs[acs[:, 2]>0, :]
    print("Annot covered total, bite, drink, bite_ambigious, drink_ambigous: {}, {}, {}, {}, {}".format(len(acs), 
                                                                                                      np.sum(acs[:,-2]==1),
                                                                                                      np.sum(acs[:,-2]==2),
                                                                                                      np.sum((acs[:,-2]==1) &(acs[:,-1]>0)),
                                                                                                      np.sum((acs[:,-2]==2) &(acs[:,-1]>0))))
                                                                                                       
    print("Mps counts total, x, neg, bite, drink, bite_ambigious, drink_ambigous: {}, {}, {}, {}, {}, {}, {}".format(len(mps), 
                                                                                                      np.sum(mps[:,-2]<0),
                                                                                                      np.sum(mps[:,-2]==0),
                                                                                                      np.sum(mps[:,-2]==1),
                                                                                                      np.sum(mps[:,-2]==2),
                                                                                                      np.sum((mps[:,-2]==1) &(mps[:,-1]>0)),
                                                                                                      np.sum((mps[:,-2]==2) &(mps[:,-1]>0))))                                                                                                       
    


# In[ ]:


def get_min_points_all_free(ds, xth, min_bite_interval, min_stay, boundary_offset, label_max_distance, label_buffer_distance, block_print=True):    
    if block_print:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating window indices...")
    mps = np.zeros((0, 5), dtype=np.int32)    
    for subj in range(len(ds)):        
        for sess in range(len(ds[subj])):
            print("\nSubject, Session: ", subj, sess)            
            x = ds[subj][sess][:, -3]
            
            pts =  msegu.find_min_points_by_xth(x, xth=xth, min_bite_interval=min_bite_interval, min_stay = min_stay, boundary_offset=boundary_offset)            
            labels = np.zeros((len(pts), 2), dtype=np.int32)
                        
            ssp = np.zeros((len(pts), 5), dtype=np.int32)
            ssp[:, 0] = subj
            ssp[:, 1] = sess
            ssp[:, 2] = pts            
            ssp[:, 3:] = labels
            mps = np.concatenate((mps, ssp), axis=0)
            print("Mps shape: ", mps.shape)
            
    if block_print:
        sys.stdout.close()        
        sys.stdout = old_stdout
        
    return mps.astype(int)


# In[5]:


def get_min_points_all(ds, annots, xth, min_bite_interval, min_stay, boundary_offset, label_max_distance, label_buffer_distance, block_print=True):    
    if block_print:
        old_stdout = sys.stdout
        sys.stdout = open(os.devnull, 'w')
    
    print("Generating window indices...")
    mps = np.zeros((0, 5), dtype=np.int32)
    acs = np.zeros((0, 5), dtype=np.int32)
    for subj in range(len(ds)):        
        for sess in range(len(ds[subj])):
            print("\nSubject, Session: ", subj, sess)            
            x = ds[subj][sess][:, -3]
            
            pts =  msegu.find_min_points_by_xth(x, xth=xth, min_bite_interval=min_bite_interval, min_stay = min_stay, boundary_offset=boundary_offset)            
            labels, annot_cover = get_labels_lab(pts, annots[subj][sess], label_max_distance, label_buffer_distance)
                        
            ssp = np.zeros((len(pts), 5), dtype=np.int32)
            ssp[:, 0] = subj
            ssp[:, 1] = sess
            ssp[:, 2] = pts            
            ssp[:, 3:] = labels
            mps = np.concatenate((mps, ssp), axis=0)
            
            ssac = np.zeros((len(annot_cover), 5), dtype=np.int32)
            ssac[:, 0] = subj
            ssac[:, 1] = sess
            ssac[:, 2] = annot_cover
            ssac[:, 3] = annots[subj][sess][:, 2]
            ssac[:, 4] = annots[subj][sess][:, -1]
            acs = np.concatenate((acs, ssac), axis=0)
            
            print_label_summary(ssp, ssac)
            
    if block_print:
        sys.stdout.close()        
        sys.stdout = old_stdout
        
    return mps.astype(int), acs.astype(int)


# In[4]:


def get_window_data(ds, indices, win_size, offset=0):   
    
    count = len(indices)
    w = np.zeros((count, win_size, 9))
    wr = np.zeros((count, win_size, 9))
    features = np.zeros((count, 93))    
    
    for i in range(count):
        subj, sess, ix = indices[i, 0], indices[i, 1], indices[i, 2]
        w[i, :, :] = ds[subj][sess][ix-win_size+offset:ix+offset, 4:]
        wr[i, :, :] = np.flip(ds[subj][sess][ix-offset:ix+win_size-offset, 4:], axis=0)
        '''
        f = mfeatu.get_features(w[i, :, :])
        fr = mfeatu.get_features(wr[i, :, :])
        gxyz = ds[subj][sess][ix, -3:]
        f = np.concatenate((f, fr, gxyz))        
        features[i, :] = f
        
        '''
    
            
    #print(w.shape,features.shape)
    assert len(w) == len(features)
    return w, wr, features


# In[ ]:



            
    

