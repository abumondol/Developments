
# coding: utf-8

# In[ ]:


def normalize_data(d):
    mu = np.mean(d)
    sigma = np.var(d)
    d = (d-mu)/sigma
    print("Mu, sigma: ", mu, sigma)
    return d, mu, sigma


# In[ ]:


def cluster_dbscan_gatech(bites, minPts, eps):    
    count = len(bites)
    nbc = np.zeros((count, 2))
    for i in range(count):
        j = i-1
        while j>=0 and bites[i] - bites[j]<=eps:
            j = j-1  
            
        nbc[i, 0] = j+1 
        
        j = i+1
        while j<count and bites[j] - bites[i]<=eps:
            j = j+1
            
        nbc[i, 1] = j-1 
            
            
    core_pt = ( (nbc[:, 1] - nbc[:,0] + 1) >=minPts )    
        
    clusters = []    
    si, last_cpi  = -1, -1
    for i in range(0, len(bites)):        
        if last_cpi < 0:            
            if core_pt[i]:
                last_cpi = i
                si = nbc[i, 0]    
                
            continue            
            
        if bites[i] - bites[last_cpi] > eps:
            if si>=0 and ( (not core_pt[i]) or (bites[i] - bites[i-1] > eps) ):
                clusters.append([si, i-1])                
                si = -1
                
            
            if core_pt[i] and si<0:                
                si = nbc[i, 0]
        
        if core_pt[i]:
            last_cpi = i    
    
    if si>=0:
        clusters.append([si, len(bites)-1])                
    
    clusters = np.array(clusters).astype(int)    
    res = np.zeros(clusters.shape).astype(int)    
    for i in range(clusters.shape[0]):
        res[i, 0] = bites[clusters[i, 0]]
        res[i, 1] = bites[clusters[i, 1]]
        
    return res


# In[ ]:


def cluster_dbscan_eat(events, minPts, eps):
    durations = events[:,1]-events[:,0]+1
    core_pt1 = (durations>=minPts) & (events[:, -1]!=0)
    core_pt2 = (durations>=16*60*5)
    core_pt = core_pt1 | core_pt2
    
    clusters = []
    
    si, last_cpi  = -1, -1        
    for i in range(0, len(events)):        
        if last_cpi < 0:            
            if core_pt[i]:
                last_cpi = i
                si = i
                while si>=0 and (events[i, 0] - events[si, 1])<=eps:
                    si -= 1
                si +=1
                
            continue
            
            
        if events[i, 0] - events[last_cpi, 1] > eps:
            if si>=0 and ( (not core_pt[i]) or (events[i, 0] - events[i-1, 1] > eps) ):
                clusters.append([si, i-1])                
                si = -1
                
            
            if core_pt[i] and si<0:                
                si = i 
                while si>=0 and (events[i, 0] - events[si, 1])<=eps:
                    si -= 1
                          
                si +=1
        
        if core_pt[i]:
            last_cpi = i    
    
    if si>=0:
        clusters.append([si, len(events)-1])                
    
    clusters = np.array(clusters).astype(int)    
    res = np.zeros(clusters.shape).astype(int)    
    for i in range(clusters.shape[0]):
        res[i, 0] = events[clusters[i, 0], 0]
        res[i, 1] = events[clusters[i, 1], 1]
        
    return res

