
# coding: utf-8

# In[ ]:


import numpy as np
import os
import pickle


# In[ ]:


def create_directory(path):
    if not os.path.exists(path):
        print('Creating directory: ', path)
        os.makedirs(path)
        
def get_one_hot(labels, num_classes):
    count = len(labels)
    res = np.zeros((count, num_classes), dtype=int)
    res[np.arange(count), labels] = 1
    return res

def param_string(params):
    keys = sorted(params.keys())
    s =""
    for key in keys:
        s += key.replace("_", "") + "_" + str(params[key]).replace(".","") + "_"
    
    s = s[:-1]
    return s


# In[ ]:


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


# In[ ]:


def remove_min_points_at_boundary(mps, data_len, offset):    
    si, ei = 0, len(mps)-1
    mp_count = len(mps)
    
    while si<mp_count and mps[si]-offset<0:
        si += 1
    
    while ei>=0 and mps[ei]+offset>=data_len:
        ei -= 1
        
    mps = mps[si:ei+1]
    return mps
    


# In[ ]:


def get_variance(windows, start_column, end_column):
    #a = windows.shape[1]//3
    #b = 2*a    
    #windows = windows[:, a:b, start_column:end_column+1]
    windows = windows[:, :, start_column:end_column+1]
    
    count = len(windows)
    v = np.zeros((count,))    
    for i in range(count):         
        v[i] = np.sum(np.var(windows[i, :, :], axis = 0))        
        
    return v 


# In[ ]:


def get_labels_lab(mps, annots, max_distance):           
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
        left_index, right_index = mps[i] - max_distance, mps[i] + max_distance        
        
        while left_index > annots[annot_index, 0]:
            annot_index += 1
            if annot_index == annot_count:
                break
        
        if annot_index == annot_count:
                break
        
        if left_index <= annots[annot_index, 0] <= right_index:
            labels[i] = annots[annot_index, 1]
            
            j = annot_index            
            while j<annot_count and annots[j, 0]<=right_index:
                annot_covered[j] += 1
                j+=1

    mp_index = 0
    for i in range(annot_count):
        if annot_covered[i] == 0:
            while mp_index < mp_count:
                if annots[i,0]-2*max_distance <= mps[mp_index] <= annots[i,0]+2*max_distance and labels[mp_index]==0:
                    labels[mp_index] = -1
                if mps[mp_index] > annots[i,0]+2*max_distance:
                    break
                mp_index+=1
    
            
    labels =labels.astype(int)    
    return labels, annot_covered



# In[ ]:


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


# In[ ]:


def get_coverage_stat(mps, annots, max_distance):    
    mp_count, annot_count = len(mps), len(annots)
    mp_cov, annot_cov = np.zeros((mp_count, 5), dtype=int), np.zeros((annot_count, 5), dtype=int)-1

    last_index = 0
    for i in range(mp_count):        
        while last_index < annot_count and annots[last_index, 0] < mps[i]-max_distance:
            last_index += 1
            
        if last_index == annot_count:
            break
            
        j = last_index
        while j<annot_count and mps[i]-max_distance <= annots[j, 0] <= mps[i]+max_distance:
            mp_cov[i, j-last_index] = annots[j, 1]
            j+=1
            
    
    last_index = 0
    for i in range(annot_count):        
        while last_index < mp_count and mps[last_index] < annots[i,0]-max_distance:
            last_index += 1
            
        if last_index == mp_count:
            break
            
        j = last_index
        while j<mp_count and annots[i, 0]-max_distance <= mps[j] <= annots[i, 0]+max_distance:
            annot_cov[i, j-last_index] = j
            j+=1
    
    mp_index = 0
    for i in range(annot_count):
        if annot_cov[i, 0] == -1:
            while mp_index < mp_count:
                if annots[i,0]-2*max_distance <= mps[mp_index] <= annots[i,0]+2*max_distance and mp_cov[mp_index,0]==0:
                    mp_cov[mp_index,0] = -1
                    annot_cov[i, 0] = -10
                if mps[mp_index] > annots[i,0]+2*max_distance:
                    break
                mp_index+=1
    
    return mp_cov, annot_cov


# In[ ]:


def get_windows(data, mps, window_size):    
    half_window = window_size//2
    mp_count = len(mps)
    windows = np.zeros((mp_count, window_size, data.shape[1]))    
    
    for i in range(mp_count):    
        ix = mps[i]        
        windows[i, :, :] = data[ix-half_window:ix+half_window, :]
        
    return windows            


# In[ ]:


def normalize_windows(windows):
    count = len(windows)    
    for i in range(count):         
        accel = windows[i, :, 3:6]        
        accel = (accel+19.6)/(2*19.6)
        accel[accel>1] = 1
        accel[accel<0] = 0
        windows[i, :, 3:6] = accel
    return windows 


# In[ ]:


def get_windows_labels_for_dataset(ds, params):
    x_th = params["x_th"]
    min_bite_interval, window_size = params["min_bite_interval"], params["window_size"]
    
    print('Creating windows and labels ...', end =" ")
    print('x_th: {}, min_bite_interval: {}, window_size: {}'.format(x_th, min_bite_interval, window_size))    
    windows_mid, windows_left, windows_right, subject_session_mp_label, features = [], [], [], [], []

    
    for subject in range(len(ds)):
        for sess in range(len(ds[subject])):
            #print('\nGenerating windows >> Subject, Sess:', subject, sess)
            data = ds[subject][sess]['data']
            annots = ds[subject][sess]['annots']
            data = data[:, :7]
                        
            mps = find_min_points_by_xth(data[:, 1], x_th, min_bite_interval)            
            mps = remove_min_points_at_boundary(mps, len(data), window_size//2)
            
            w_mid   = get_windows(data[:, 1:], mps, window_size)
            #w_left  = get_windows(data[:, 1:], mps-window_size//2, window_size)
            #w_right = get_windows(data[:, 1:], mps+window_size//2, window_size)
            
            v = get_variance(w_mid, 3, 5)    # 3, 4,  5: Accelerometer X, Y, Z axes
            v = v.reshape((len(v), 1))
            grav_xyz_at_mps = data[mps, 1:4]
            
            w_mid = normalize_windows(w_mid)
            
            ssml = np.zeros((len(mps), 4))            
            ssml[:, 1] = sess
            ssml[:, 2] = mps
            
            if annots.shape[1]==2:
                l, _ = get_labels_lab(mps, annots, max_distance=3*16)
                ssml[:, 0] = subject                
            else:
                l = get_labels_free(mps, annots, window_size)
                if subject<5:
                    ssml[:, 0] = subject + 2            
                else:
                    ssml[:, 0] = subject + 100
                                    
            ssml[:, 3] = l
            
            f = np.concatenate((grav_xyz_at_mps, v), axis=1)
            if len(windows_mid)==0:
                windows_mid = w_mid
                #windows_left = w_left
                #windows_right = w_right
                subject_session_mp_label = ssml
                features = f                
            else:
                windows_mid = np.concatenate((windows_mid, w_mid), axis=0)
                #windows_left = np.concatenate((windows_left, w_left), axis=0)
                #windows_right = np.concatenate((windows_right, w_right), axis=0)
                subject_session_mp_label = np.concatenate((subject_session_mp_label, ssml), axis=0)
                features = np.concatenate((features, f), axis=0)
                
    
    ssml = subject_session_mp_label.astype(int)    
    #windows = {"mid":windows_mid, "left":windows_left, "right":windows_right}
    
    return windows_mid, ssml, features


# In[ ]:


def get_windows_lab(params):
    
    if "C:" in os.getcwd():
        path ='C:/ASM/DevData/eating'
    else:
        path = "."    
    
    with open(path+'/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
        lab_data = pickle.load(file)
    
    return get_windows_labels_for_dataset(lab_data, params)    

