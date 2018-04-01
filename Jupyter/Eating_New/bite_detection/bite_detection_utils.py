
# coding: utf-8

# In[1]:


import numpy as np
import os


# In[3]:


def create_directory(path):
    if not os.path.exists(path):
        print('Creating directory: ', path)
        os.makedirs(path)
        
def get_one_hot(labels, num_classes):
    count = len(labels)
    res = np.zeros((count, num_classes), dtype=int)
    res[np.arange(count), labels] = 1
    return res
    


# In[2]:


def find_min_points_by_xth(x, x_th, min_bite_interval):        
    step_length = min_bite_interval//2        
    count = len(x)
    
    mps = []
    for i in range(0, count-step_length, step_length):
        min_index = i + np.argmin(x[i:i+step_length])        
        if x[min_index] <= x_th:
            mps.append(min_index)
    
    if len(mps)<=1:
        return mps
    
    while True:
        res = []
        count = len(mps)
        ix = mps[0]
        ixRight = mps[1]
        if ixRight - ix > min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)
        
        for i in range(1, count - 1):
            ix = mps[i]
            ixLeft = mps[i - 1]
            ixRight = mps[i + 1]

            cond_left = ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]
            cond_right = ixRight - ix > min_bite_interval or x[ix] < x[ixRight]        

            if cond_left and cond_right:
                res.append(ix)
        
        ix = mps[count - 1]
        ixLeft = mps[count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]:
            res.append(ix)            
        
        if len(mps) == len(res):
            break        
        mps = res        
    
    mps = np.array(mps).astype(int)    
    return mps


# In[3]:


def remove_min_points_at_boundary(mps, data_len, offset):    
    si, ei = 0, len(mps)-1
    mp_count = len(mps)
    
    while si<mp_count and mps[si]-offset<0:
        si += 1
    
    while ei>=0 and mps[ei]+offset>=data_len:
        ei -= 1
        
    mps = mps[si:ei+1]
    return mps
    


# In[4]:


def get_variance(windows, start_column, end_column):    
    count = len(windows)
    v = np.zeros((count,))
    
    for i in range(count):         
        v[i] = np.sum(np.var(windows[i, :, start_column:end_column+1], axis = 0))        
        
    return v 


# In[5]:


def get_labels_lab(mps, annots, window_size):       
    half_window = window_size//2
    mp_count = len(mps)    
    if mp_count == 0:
        return mps
    
    labels = np.zeros((mp_count, ))      
    annot_count = len(annots)
    annot_covered = np.zeros((annot_count, ))
    
    if annot_count==0:
        return labels, annot_covered
    
    annot_index = 0    
    for i in range(mp_count):
        left_index, right_index = mps[i] - half_window, mps[i] + half_window-1        
        
        while left_index > annots[annot_index, 0]:
            annot_index += 1
            if annot_index == annot_count:
                return labels, annot_covered            
            
        if left_index <= annots[annot_index, 0] <= right_index:
            labels[i] = annots[annot_index, 1]
            
            j = annot_index            
            while j<annot_count and annots[j, 0]<=right_index:
                annot_covered[j] += 1
                j+=1
            
    labels =labels.astype(int)    
    return labels, annot_covered


# In[6]:


def get_labels_free(mps, annots, window_size):       
    half_window = window_size//2
    mp_count = len(mps)
    if mp_count == 0:
        return mps
    
    labels = np.zeros((mp_count, ))      
    annot_count = len(annots)
    if annot_count==0:
        return lables
    
    annot_index = 0
    for i in range(mp_count):                        
        while mps[i] > annots[annot_index, 1]:
            annot_index += 1
            if annot_index == annot_count:
                return labels
            
        if mps[i] >= annots[annot_index, 0]:
                labels[i] = annots[annot_index, 2]            
    
    labels =labels.astype(int)
    return labels


# In[7]:


def get_windows(data, mps, window_size):    
    half_window = window_size//2
    mp_count = len(mps)
    windows = np.zeros((mp_count, window_size, data.shape[1]))    
    
    for i in range(mp_count):    
        ix = mps[i]        
        windows[i, :, :] = data[ix-half_window:ix+half_window, :]
        
    return windows            


# In[9]:


def get_windows_labels_for_dataset(ds, x_th=-0.3, var_th=0.25, min_bite_interval=2*16, window_size=6*16):    
    print('Creating windows and labels ...')
    print('x_th, var_th: ', x_th, var_th)    
    print('min_bite_interval, window_size: ', min_bite_interval, window_size)
    

    windows, windows_left, windows_right, subject_session_mp_label = [], [], [], []
    
    for subject in range(len(ds)):
        for sess in range(len(ds[subject])):
            #print('\nGenerating windows >> Subject, Sess:', subject, sess)
            data = ds[subject][sess]['data']
            annots = ds[subject][sess]['annots']                        
            
            mps = find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)            
            mps = remove_min_points_at_boundary(mps, len(data), window_size)
            
            w = get_windows(data[:, 1:], mps, window_size)            
            v = get_variance(w, 3, 5)    # 3, 4,  5: Accelerometer X, Y, Z axes
            mps = mps[v>=var_th]
                        
            w = get_windows(data[:, 1:], mps, window_size)
            w_left = get_windows(data[:, 1:], mps-window_size//2, window_size)
            w_right = get_windows(data[:, 1:], mps+window_size//2, window_size)
            
            ssml = np.zeros((len(mps), 4))            
            ssml[:, 1] = sess
            ssml[:, 2] = mps
            
            if annots.shape[1]==2:
                l, _ = get_labels_lab(mps, annots, window_size)
                ssml[:, 0] = subject                
            else:
                l = get_labels_free(mps, annots, window_size)
                if subject<5:
                    ssml[:, 0] = subject + 2            
                else:
                    ssml[:, 0] = subject + 100
                                    
            ssml[:, 3] = l
            
            if len(windows)==0:
                windows = w
                windows_left = w_left
                windows_right = w_right
                subject_session_mp_label = ssml               
            else:
                windows = np.concatenate((windows, w), axis=0)
                windows_left = np.concatenate((windows_left, w_left), axis=0)
                windows_right = np.concatenate((windows_right, w_right), axis=0)
                subject_session_mp_label = np.concatenate((subject_session_mp_label, ssml), axis=0)
    
    ssml = subject_session_mp_label.astype(int)
    labels = get_one_hot(ssml[:,-1], num_classes=3)
    return windows, windows_left, windows_right, ssml, labels

