
# coding: utf-8

# In[1]:


import numpy as np
import pickle
import sys


# In[6]:


print("************* Bite Detection Parameters***************")
print('Weighted: ', weighted)
print('Include free data: ', include_free_data)
print('Num epochs: ', epochs)
print('xth: ', x_th)
print('var_th: ', var_th)
print('min_bite_interval: ', min_bite_interval)
print('Window size: ', window_size)


# In[7]:


def create_directory(path):
    if not os.path.exists(model_path):
        print('Creating directory: ', model_path)
        os.makedirs(model_path)


# In[8]:


def process_annots(annots, min_distance):    
    count = len(annots)
    flags = np.ones((count, ))
    
    for i in range(1, count):
        if annots[i, 0] - annots[i-1, 0]<=min_distance:
            flags[i-1] = 0
            
    annots = annots[flags==1]
    #print('Annnot prcess: before, after :: ', count, len(annots))
    return annots
            
    
def process_annots_dataset(ds, min_distance):
    for subject in range(len(ds)):
        for sess in range(len(ds[subject])):            
            annots = ds[subject][sess][1]
            ds[subject][sess][1] = process_annots(annots, min_distance)            
    return ds
    
def process_uva_lab_data(data, min_distance):
    #usc:0-13, uva: 2, 5, 2, 2, 4, 5, 1
    data[14] = [data[14][0], data[15][0]]
    data[15] = [data[16][0], data[17][0], data[18][0], data[19][0], data[20][0]]
    data[16] = [data[21][0], data[22][0]]
    data[17] = [data[23][0], data[24][0]]
    data[18] = [data[25][0], data[26][0], data[27][0], data[28][0]]
    data[19] = [data[29][0], data[30][0], data[31][0], data[32][0], data[33][0]]
    data[20] = data[34]
    data = data[:21]
    data = process_annots_dataset(data, min_distance)
    return data


# In[9]:


with open(root_path + "data/steven_lab_data_800.pkl", 'rb') as file:
    lab_data = pickle.load(file)
with open(root_path + "data/uva_lab_data_800.pkl", 'rb') as file:
    uva_lab_data = pickle.load(file)
    uva_lab_data = process_uva_lab_data(uva_lab_data, 16)
lab_data.extend(uva_lab_data)
print('Subject counts total Lab: ', len(lab_data))


# In[10]:


if include_free_data:
    with open(root_path + "data/steven_free_data_800.pkl", 'rb') as file:
        free_data = pickle.load(file)
    print('Subject counts total Free: ', len(free_data))


# In[11]:


#***************************************************************************************************#


# In[12]:


def find_min_points_by_xth(x, x_th, min_bite_interval):        
    step_length = min_bite_interval//2        
    count = len(x)
    
    mp = []
    for i in range(0, count-step_length, step_length):
        min_index = i + np.argmin(x[i:i+step_length])        
        if x[min_index] <= x_th:
            mp.append(min_index)
    
    if len(mp)<=1:
        return mp
    
    while True:
        res = []
        count = len(mp)
        ix = mp[0]
        ixRight = mp[1]
        if ixRight - ix > min_bite_interval or x[ix] < x[ixRight]:
            res.append(ix)
        
        for i in range(1, count - 1):
            ix = mp[i]
            ixLeft = mp[i - 1]
            ixRight = mp[i + 1]

            cond_left = ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]
            cond_right = ixRight - ix > min_bite_interval or x[ix] < x[ixRight]        

            if cond_left and cond_right:
                res.append(ix)
        
        ix = mp[count - 1]
        ixLeft = mp[count - 2]
        if ix - ixLeft > min_bite_interval or x[ix] <= x[ixLeft]:
            res.append(ix)            
        
        if len(mp) == len(res):
            break        
        mp = res        
    
    return mp


# In[13]:


def remove_min_points_at_boundary(mp, data_len, window_size):
    half_window = window_size//2
    si, ei = 0, len(mp)-1
    mp_count = len(mp)
    
    while si<mp_count and mp[si]-half_window<0:
        si += 1
    
    while ei>=0 and mp[ei]+half_window>data_len:
        ei -= 1
        
    mp = mp[si:ei+1]
    return mp
    


# In[14]:


def filter_windows_by_feature(windows, labels, var_th):    
    count = len(labels)
    flags = np.full((count,), False, dtype=bool)
    
    for i in range(count):         
        v = np.sum(np.var(windows[i, :, :], axis = 0))        
        flags[i] = (v >= var_th)
        
    windows = windows[flags, :, :]
    labels = labels[flags]
    return windows, labels 


# In[15]:


def get_labels_lab(mp, annots, window_size):       
    half_window = window_size//2
    mp_count = len(mp)    
    if mp_count == 0:
        return mp
    
    labels = np.zeros((mp_count, ))      
    annot_count = len(annots)
    if annot_count==0:
        return labels
    
    annot_index = 0
    for i in range(mp_count):
        left_index, right_index = mp[i] - half_window, mp[i] + half_window-1        
        
        while left_index > annots[annot_index, 0]:
            annot_index += 1
            if annot_index == annot_count:
                return labels            
            
        if left_index <= annots[annot_index, 0] <= right_index:
            labels[i] = annots[annot_index, 1]           
            
    return labels


# In[16]:


def get_labels_free(mp, annots, window_size):       
    half_window = window_size//2
    mp_count = len(mp)
    if mp_count == 0:
        return mp
    
    labels = np.zeros((mp_count, ))      
    annot_count = len(annots)
    if annot_count==0:
        return lables
    
    annot_index = 0
    for i in range(mp_count):                        
        while mp[i] > annots[annot_index, 1]:
            annot_index += 1
            if annot_index == annot_count:
                return labels
            
        if mp[i] >= annots[annot_index, 0]:
                labels[i] = annots[annot_index, 2]            
        
    return labels


# In[17]:


def get_windows(data, mp, window_size):    
    half_window = window_size//2
    mp_count = len(mp)
    windows = np.zeros((mp_count, window_size, data.shape[1]))    
    
    for i in range(mp_count):    
        ix = mp[i]        
        windows[i, :, :] = data[ix-half_window:ix+half_window, :]
        
    return windows            


# In[26]:


def get_windows_labels_for_dataset(ds, x_th=-3, var_th=0.5, min_bite_interval=32, window_size=6*16):
    print('Weighted: ', weighted)
    print('Include free data: ', include_free_data)    
    print('xth: ', x_th)
    print('var_th: ', var_th)
    print('min_bite_interval: ', min_bite_interval)
    print('Window size: ', window_size)

    windows = []
    labels = []    
    subject_session = []    
    for subject in range(len(ds)):
        for sess in range(len(ds[subject])):
            print('\nGenerating windows >> Subject, Sess:', subject, sess)
            data = ds[subject][sess][0]
            annots = ds[subject][sess][1]            
            data = data[:, 1:]            
            print("   Actual Sample count, Bites, Sips, total: ", data.shape, np.sum(annots[:,1]==1), np.sum(annots[:,1]==2), annots.shape)
            
            
            mp = find_min_points_by_xth(data[:, 0], x_th, min_bite_interval)            
            mp = remove_min_points_at_boundary(mp, len(data), window_size)
            print("   mp count after boundary filter: ", len(mp))
            w = get_windows(data, mp, window_size)            
                
            if annots.shape[1]==2:
                l = get_labels_lab(mp, annots, window_size)
            else:
                l = get_labels_free(mp, annots, window_size)            
            
            total, negs, bites, sips = len(l),  np.sum(l==0), np.sum(l==1), np.sum(l==2)            
            print("   Before feature filter: total_windows, Negatives, Bites, Sips: ", total, negs, bites, sips)
            
            w, l = filter_windows_by_feature(w, l, var_th)            
            ss = np.zeros((len(l), 3))            
            ss[:, 1] = sess
            
            total, negs, bites, sips = len(l),  np.sum(l==0), np.sum(l==1), np.sum(l==2)            
            print("   After feature filter: total_windows, Negatives, Bites, Sips: ", total, negs, bites, sips)
            
            if annots.shape[1]==2:
                ss[:, 0] = subject
            elif subject<5:
                ss[:, 0] = subject + 2            
            else:
                ss[:, 0] = subject + 100
            
            if len(windows)==0:
                windows = w
                labels = l                
                subject_session = ss                
            else:
                windows = np.concatenate((windows, w), axis=0)
                labels = np.concatenate((labels, l), axis=0)                
                subject_session = np.concatenate((subject_session, ss), axis=0)
                
    return windows, labels, subject_session


# In[27]:


get_windows_labels_for_dataset(lab_data)


# In[119]:


def get_all_windows(x_th, var_th, min_bite_interval, window_size, include_free_data):
    windows, labels, subject_session = get_windows_labels_for_dataset(lab_data, x_th, var_th, min_bite_interval, window_size)
    if include_free_data:        
        w, l, ss = get_windows_labels_for_dataset(free_data, x_th, var_th, min_bite_interval, window_size)
        w = w[l==0, :, :]
        ss = ss[l==0, :]
        l = l[l==0]
        windows = np.concatenate((windows, w), axis=0)
        labels = np.concatenate((labels, l), axis=0)                
        subject_session = np.concatenate((subject_session, ss), axis=0)
        
    return windows, labels, subject_session


# In[120]:


x_th, var_th, min_bite_interval, window_size, include_free_data = -3, 0.5, 32, 6*16, False
windows, labels, subject_session = get_all_windows(x_th, var_th, min_bite_interval, window_size, include_free_data)
windows = (windows+9.8)/(2*9.8)
windows[windows>1]=1
windows[windows<0]=0
windows = windows.reshape((windows.shape[0], windows.shape[1], windows.shape[2], 1))
total, negs, bites, sips = len(labels),  np.sum(labels==0), np.sum(labels==1), np.sum(labels==2)
class_weights=[total/negs, total/bites, total/bites]/(total/negs)
print("total_windows, Negatives, Bites, Sips, class_wights: ", total, negs, bites, sips, class_weights)

