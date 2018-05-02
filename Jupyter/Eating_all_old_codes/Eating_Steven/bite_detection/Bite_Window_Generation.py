
# coding: utf-8

# In[92]:


import numpy as np
import pickle
import os
import matplotlib.pyplot as plt


# In[93]:


with open('C:/ASM/DevData/eating_steven/data/lab_data_steven.pkl', 'rb') as file:
    ds = pickle.load(file)


# In[94]:


def get_segments(x, xth):
    #return segs as indices
    count = len(x)
            
    y = (x<=xth).astype(int)    
    y = y[:-1] - y[1:]
    
    inside = 1 + np.argwhere(y==-1)
    outside = np.argwhere(y==1)
    #print(inside.shape, outside.shape)
    
    if inside[0]>outside[0]:
        outside = outside[1:]
    
    if inside[-1]>outside[-1]:
        inside = inside[:-1]
        
    assert len(inside)==len(outside)   
    res = np.concatenate((inside, outside), axis=1).astype(int)    
    return res    


# In[95]:


def get_segments_test(d, a, xth):
    sg = get_segments(d[:, -3], xth)
    segs = sg[(sg[:, 1]-sg[:,0])>=8, :]
    count = len(segs)
    
    mins = np.zeros((count,))
    covs = np.zeros((count,2))
        
    bi = a[a[:, 2]==1, 0]
    sp = a[a[:, 2]==2, :]
    sp1, sp2 = sp[:,0], sp[:, 1]    
    
    assert len(bi)+len(sp) == len(a)    
    
    offset = 0
    for i in range(count):
        s, e = segs[i, 0], segs[i, 1]
        mins[i] = np.amin(d[s:e+1, -3])
        
        covs[i, 0] = np.sum((s-offset<=bi)&(bi<=e+offset))
        covs[i, 1] = np.sum( ((s-offset<=sp1)&(sp1<=e+offset)) | ((s-offset<=sp2)&(sp2<=e+offset)) )
                
    print("Seg count:", sg.shape, segs.shape)    
    print("Bite Count, Covered: ", bi.shape[0], np.sum(covs[:,0]))   
    print("Sip  Count, Covered: ", sp.shape[0], np.sum(covs[:,1]))
    
    
    return segs, covs


# In[96]:


def get_window_indices_segs(d, a, win_size, xth):
    sg = get_segments(d[:, -3], xth)
    segs = sg[(sg[:, 1]-sg[:,0])>=8, :]
    #print("Seg count Before: ", len(segs))
    covs = np.zeros((len(segs),2), dtype=np.int32)    
    
    bi = a[a[:, 1]==0, 0]
    sp = a[a[:, 1]>0, :]
    sp1, sp2 = sp[:,0], sp[:, 1]   
    offset = 0
    for i in range(len(segs)):
        s, e = segs[i, 0], segs[i, 1]
        covs[i, 0] = np.sum((s-offset<=bi)&(bi<=e+offset))
        covs[i, 1] = np.sum( ((s-offset<=sp1)&(sp1<=e+offset)) | ((s-offset<=sp2)&(sp2<=e+offset)) )
    segs = np.concatenate((segs, covs), axis=1)
    
    half_win_size = win_size//2
    dcount = len(d)
    while segs[0, 0] < half_win_size:
        segs = segs[1:, :]
    
    while segs[-1, 1] > dcount - half_win_size:
        segs = segs[:-1, :]
        
    #print("Seg count After: ", segs.shape)
        
    total_count = np.sum((segs[:,1]+1) -segs[:, 0])
    total_count = int(total_count)
    w_indices = np.zeros((total_count, 4))    
    k = 0
    for i in range(len(segs)):
        s, e = segs[i,0], segs[i, 1]        
        
        for j in range(s, e+1):
            #w[k, :, :] = d[j-half_win_size:j+half_win_size, 1:]
            w_indices[k, 0] = j            
            w_indices[k, 1] = i #seg_number            
            w_indices[k, 2] = segs[i, 2] + segs[i, 3]
            w_indices[k, 3] = np.sum(np.var(d[j-half_win_size:j+half_win_size, 1:4], axis=0))
            k+=1
            
    assert k==total_count
    
    return w_indices, segs
    


# In[110]:


def get_labels(w_indices, a, segs, win_size=5*16):
    wcount, acount = len(w_indices), len(a)
    labels = np.zeros((wcount, 3))        
    
    mid = (w_indices[:, 0]).astype(int)
    left = mid-win_size//2
    right = mid+win_size//2
    
    #put don't care around all the annots
    for i in range(acount):
        si, ei = a[i, 0], a[i, 1]
        si, ei = si-win_size//2, ei+win_size//2        
        cond = ((left>=si) & (left<=ei)) | ((right>=si) & (right<=ei))
        labels[cond, 0] = -1
    
    
    #not put bite/sip annotations around propoer annots
    seg_starts = segs[:,0]
    seg_ends = segs[:,1]
    repeat_count, outside_seg_count = 0, 0
    for i in range(acount):
        if a[i, -1]!=0: # don't consider ambigious annots
            repeat_count+=1
            continue        
            
        #check if the annot is within any segments or not. If not, continue
        si, ei = a[i, 0], a[i, 1]
        if si==ei:
            flag = np.sum( ((seg_starts<=si)&(si<=seg_ends)))
        else:
            flag = np.sum( ((seg_starts<=si)&(si<=seg_ends)) | ((seg_starts<=ei)&(ei<=seg_ends)) )
            
        if flag==0:
            outside_seg_count+=1
            continue
        
        #put labels to the windows
        si, ei = si-win_size//4, ei+win_size//4        
        cond = ((mid>=si) & (mid<=ei))
        labels[cond, 0] = a[i, 2]
        labels[cond, 1] = labels[cond, 1] + 1 #covered by how many annots
        labels[cond, 2] = (i+1) #covered annot index, starting from one
    
    print("Repeat annots, Outside seg annots: ", repeat_count, outside_seg_count)
    return labels


# In[111]:


windows = []
for subj in range(len(ds)):
    ws = []
    for sess in range(len(ds[subj])):
        print("\n", subj, sess)
        
        d = np.copy(ds[subj][sess]["data_right"])
        a = np.copy(ds[subj][sess]["annots"])        
        a = a[a[:, -2]!=2, :]  #Exclude left hand      
        print("Annot count: ", len(a))
        
        if subj==0 and sess==1:
            cond = a[:,0]>=9508
            a[cond, 0] = a[cond, 0] + int(1.6*16) #add offset
            a[cond, 1] = a[cond, 1] + int(1.6*16) #add offset
            
        if subj==2 and sess==0:
            cond = a[:,0]>=9215
            #a = a[cond, :] 
            
        if subj==5 and sess==1:
            cond = a[:,0]>=11717
            a[cond, 0] = a[cond, 0] + int(1.3*16) #add offset
            a[cond, 1] = a[cond, 1] + int(1.3*16) #add offset
        
        get_segments_test(d, a, xth=-0.3)        
        w_indices, segs = get_window_indices_segs(d, a, win_size=5*16, xth=-0.3)
        labels = get_labels(w_indices, a, segs, win_size=5*16)
        
        print("Shapes w_indices, labels, segs: ", w_indices.shape, labels.shape, segs.shape)        
        print("Label stat shape, no, bite, sip, x: ", np.sum(labels[:,0]==0), np.sum(labels[:,0]==1), np.sum(labels[:,0]==2), np.sum(labels[:,0]<0))
        print("Multi cover 0, 1, 2, 3: ", np.sum(labels[:,1]==0), np.sum(labels[:,1]==1), np.sum(labels[:,1]==2), np.sum(labels[:,1]==3))
        print("Unique annots: ", len(np.unique(labels[:, 2])))
        #print(np.unique(labels[:, 2]))
        ws.append({"w_indices":w_indices, "labels":labels, "segs":segs})
        
    windows.append(ws)


# In[112]:


with open('C:/ASM/DevData/eating_steven/data/lab_windows_steven.pkl', 'wb') as file:
    pickle.dump(windows, file)

