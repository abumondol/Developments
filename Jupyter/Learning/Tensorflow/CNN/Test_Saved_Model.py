
# coding: utf-8

# In[1]:


import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data


# In[2]:


mnist = input_data.read_data_sets('/tmp/data/', one_hot = True)


# In[3]:


path = 'C:/ASM/DevData/learning/tensorflow/cnn_model'
sess = tf.Session()

loader = tf.train.import_meta_graph(path+'/my-test-model.meta')
loader.restore(sess, tf.train.latest_checkpoint(path))

graph = tf.get_default_graph()
x = graph.get_tensor_by_name('x:0')
y = graph.get_tensor_by_name('y:0')
keep_prob = graph.get_tensor_by_name('keep_prob:0')
conv1_w = graph.get_tensor_by_name('conv_1/W:0')
print(sess.run(conv1_w))


# In[ ]:


accuracy = graph.get_tensor_by_name('accuracy:0')


# In[ ]:


overall_accuracy = sess.run(accuracy, feed_dict={x:mnist.test.images, 
                                                     y:mnist.test.labels, 
                                                     keep_prob:1.0})    


# In[ ]:


print("Testing Accuracy: ", overall_accuracy)


# In[ ]:


sess.close()

