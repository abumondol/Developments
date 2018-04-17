
# coding: utf-8

# In[51]:


import numpy as np
import pickle
import sys
import pandas as pd
import importlib
import my_bite_detection_utils as bdu
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[49]:


with open('C:/ASM/DevData/eating/data/steven_uva_lab_data_combined.pkl', 'rb') as file:
    data = pickle.load(file)


# In[52]:


#importlib.reload(mtu)
importlib.reload(bdu)
#importlib.reload(mcu)
#importlib.reload(mku)


# In[53]:


res = []
sub_sess_annots = np.empty((0, 4))
sub_sess_segs = np.empty((0, 11))
for subj in range(28):
    for sess in range(len(data[subj])):
        print(subj, sess, end=" :: ")
        d = data[subj][sess]["data"]
        a = data[subj][sess]["annots"]        
        segs = bdu.get_segments(d[:, 1], -0.4, -0.4)
        
        a_count = len(a)
        seg_count = len(segs)
        
        ssa = bdu.get_sub_sess(subj, sess, a_count, 4, dtype =np.int32)        
        ssa[:, 2:] = a
        sub_sess_annots = np.concatenate((sub_sess_annots, ssa))
        
        sss = bdu.get_sub_sess(subj, sess, seg_count, 11, dtype=np.int32)        
        sss[:, 2:4] = segs
        sss[1:, 4] = segs[1:, 0] -segs[:-1, 1] 
        sss[:, 5:7] = bdu.get_covers(segs, a, 0)
        sss[:, 7:9] = bdu.get_covers(segs, a, 16)
        sss[:, 9:11] = bdu.get_covers(segs, a, 32)
        sub_sess_segs = np.concatenate((sub_sess_segs, sss))                                        
        
        seg_duration = np.sum(segs[:, 1]-segs[:,0])/16
        duration = d[-1, 0]
        
        b_count = np.sum(a[:,1]==1)
        s_count = np.sum(a[:,1]==2)
        
        covers = sss[:, 5:]
        cover_count2 = list(np.sum(covers, axis=0))
        blank_count0 = np.sum((covers[:, 0]==0) & (covers[:, 1]==0))
        blank_count1 = np.sum((covers[:, 2]==0) & (covers[:, 3]==0))
        blank_count2 = np.sum((covers[:, 4]==0) & (covers[:, 5]==0))
        multi_count0 = np.sum((covers[:, 0]+covers[:,1])>1)
        multi_count1 = np.sum((covers[:, 2]+covers[:,3])>1)
        multi_count2 = np.sum((covers[:, 4]+covers[:,5])>1)
        dupli_count0 = np.sum((covers[:, 0]>0) & (covers[:, 1]>0))
        dupli_count1 = np.sum((covers[:, 2]>0) & (covers[:, 3]>0))
        dupli_count2 = np.sum((covers[:, 4]>0) & (covers[:, 5]>0))
        
        
        r = [subj, sess, duration, a_count, b_count, s_count, seg_count, seg_duration]
        r.extend(cover_count2)
        r.extend([blank_count0, blank_count1, blank_count2, multi_count0, multi_count1, multi_count2, dupli_count0, dupli_count1, dupli_count2])
            
        #print(r)
        res.append(r)
res = np.array(res)
print(np.unique(sub_sess_segs[:,1]))


# In[55]:


from IPython.display import display, HTML
res = np.array(res)
cols = ['subj', 'sess', 'data duration', 'annot_count', 'bite_count', 'sip_count','seg_count', 'seg_duration']
cols.extend(['bcov0', 'scov0', 'bcov1', 'scov1', 'bcov2', 'scov2'])
cols.extend(['blank0', 'blank1', 'blank2', 'multi0', 'multi1', 'multi2', 'dupli0', 'dupli1', 'dupli3'])
df = pd.DataFrame(res, columns=cols)
df.append(df.sum(numeric_only=True), ignore_index=True)
df.to_csv('res.csv', sep=',')
display(df)

print(np.sum(res, axis=0).astype(int))


# In[ ]:


ssa = sub_sess_annots
sss = sub_sess_segs

a = sss[:, 4]/16
a = a[a<=10]
plt.hist(a, bins=50)
#plt.xlim([0, 5])
plt.show()

a = (sss[:, 3]-sss[:, 2])/16
b = sss[a>60, 0:4]
print(len(b))
print(b)

print(np.sum(a<=1))
a = a[a<=10]
plt.hist(a, bins=50)
#plt.xlim([0, 5])
plt.show()


# In[ ]:


sss = sub_sess_segs
dur = (sss[:, 3]-sss[:, 2])/16
print(len(dur))
b = sss[dur>60, 0:4]
print(len(b))

for i in range(10):
    subj, sess = int(b[i, 0]), int(b[i, 1])
    d = data[subj][sess]["data"]
    a = data[subj][sess]["annots"]

    si, ei = int(b[i,2]), int(b[i,3])
    print(si, ei)
    
    plt.plot(d[si:ei, 0], d[si:ei, 1:4])
    plt.grid(True)
    plt.show()


# In[46]:


sss = sub_sess_segs
c = sss[:, 9]+sss[:, 10]
s = sss[c>0]
#print(np.sum(c>0))
print(s.shape)

dur = (s[:, 3]-s[:, 2])/16
print(np.sum(dur<7))
plt.hist(dur, bins=100)
plt.show()


# In[ ]:




















