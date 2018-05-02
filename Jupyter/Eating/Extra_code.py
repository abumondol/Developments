
# coding: utf-8

# In[ ]:


def normalize_data(d):
    mu = np.mean(d)
    sigma = np.var(d)
    d = (d-mu)/sigma
    print("Mu, sigma: ", mu, sigma)
    return d, mu, sigma

