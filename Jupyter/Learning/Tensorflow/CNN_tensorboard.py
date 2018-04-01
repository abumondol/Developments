
# coding: utf-8

# In[1]:


import os
import os.path
import shutil
import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data


# In[2]:


LOGDIR = 'C:/ASM/DevData/learning/tensorboard/graphs'
mnist = input_data.read_data_sets('/tmp/data/', one_hot = True)


# In[3]:


#Training Parameters
learning_rate = 0.001
num_epochs = 200
batch_size = 128
display_step = 10

#Network Parameters
num_input = 784
num_classes = 10
keep_prob_val = 0.5


# In[4]:


def conv_layer(x, size_in, size_out, ksize, strides, padding, name):    
    strides = [1, strides[0], strides[1], 1]    
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([ksize[0], ksize[1], size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
        conv = tf.nn.conv2d(x, W, strides=strides, padding=padding)
        output = tf.nn.relu(conv + b)
        tf.summary.histogram("weights", W)
        tf.summary.histogram("biases", b)
        tf.summary.histogram("outputs", output)
        return output
    
def maxpool_layer(x, ksize, strides, padding, name):
    ksize = [1, ksize[0], ksize[1], 1]
    strides = [1, strides[0], strides[1], 1]
    with tf.name_scope(name):
        output = tf.nn.max_pool(x, ksize, strides, padding)
        tf.summary.histogram("outputs", output)
        return output
    
def fc_layer(x, size_in, size_out, name, relu=False):
    with tf.name_scope(name):
        W = tf.Variable(tf.truncated_normal([size_in, size_out], stddev=0.1), name="W")
        b = tf.Variable(tf.constant(0.1, shape=[size_out]), name="b")
        output = tf.matmul(x, W) + b
        if relu:
            output = tf.nn.relu(output)
            
        tf.summary.histogram('weights', W)
        tf.summary.histogram('biases', b)
        tf.summary.histogram('outputs', output)
        return output
    


# In[5]:


def convnet_model(x, keep_prob):
    x = tf.reshape(x, shape=[-1, 28, 28, 1])
    conv_1 = conv_layer(x, size_in=1, size_out=32, ksize=[5,5], strides=[1,1], padding="SAME", name='conv_1')
    maxpool_1 = maxpool_layer(conv_1, ksize=[2,2], strides=[2,2], padding="SAME", name="maxpool_1")
    
    conv_2 = conv_layer(maxpool_1, size_in=32, size_out=64, ksize=[5,5], strides=[1,1], padding="SAME", name='conv_2')
    maxpool_2 = maxpool_layer(conv_2, ksize=[2,2], strides=[2,2], padding="SAME", name="maxpool_2")
    
    flattened = tf.reshape(maxpool_2, shape=[-1, 7*7*64])
    
    fc_1 = fc_layer(flattened, 7*7*64, 128, name='FC_1', relu=True)
    fcdrop_1 = tf.nn.dropout(fc_1, keep_prob, name='fcdrop_1')    
    logits = fc_layer(fcdrop_1, 128, 10, name='FC_2')
    
    return logits    


# In[6]:


#tf Graph input
x = tf.placeholder(tf.float32, [None, num_input], name="x")
y = tf.placeholder(tf.float32, [None, num_classes], name="y")
keep_prob = tf.placeholder(tf.float32, name="keep_prob")


# In[7]:


logits = convnet_model(x, keep_prob)
prediction = tf.nn.softmax(logits, name="prediction") 


loss_op = tf.reduce_mean(
    tf.nn.softmax_cross_entropy_with_logits(logits=logits, labels=y), name="loss_op")
tf.summary.scalar("loss_op_summary", loss_op)


train_step = tf.train.AdamOptimizer(learning_rate).minimize(loss_op, name="train_step")


correct_prediction = tf.equal(tf.argmax(logits, 1), tf.argmax(y, 1), name="correct_prediction")
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32), name="accuracy")
tf.summary.scalar("accuracy_summary", accuracy)

summ = tf.summary.merge_all()    


# In[8]:


def create_directory(path):
    if not os.path.exists(path):
        print('Creating directory: ', path)
        os.makedirs(path)


# In[ ]:


sess = tf.Session()
sess.run(tf.global_variables_initializer())
writer = tf.summary.FileWriter(LOGDIR)
writer.add_graph(sess.graph)

for epoch in range(num_epochs):
    batch_x, batch_y = mnist.train.next_batch(batch_size)
    #print(batch_x.shape, batch_y.shape, keep_prob_val)
    
    if epoch%2==0:
        loss, train_accuracy, s = sess.run([loss_op, accuracy, summ], feed_dict={x: batch_x, y: batch_y, keep_prob:keep_prob_val})
        writer.add_summary(s, epoch)
        print('Epoch: {}, Minibatch Loss: {:.4f}, Training accuracy: {:.3f}'.format(epoch, loss, train_accuracy))
    
    sess.run(train_step, feed_dict={x:batch_x, y:batch_y, keep_prob:keep_prob_val})

print('Optimization Finished!')

overall_accuracy = sess.run(accuracy, feed_dict={x:mnist.test.images, 
                                                     y:mnist.test.labels, 
                                                     keep_prob:1.0})    
print("Testing Accuracy: ", overall_accuracy)

saver = tf.train.Saver()
path = 'C:/ASM/DevData/learning/tensorflow/cnn_model'
create_directory(path)

saver.save(sess, path+'/my-test-model')
sess.close()


# In[ ]:


path = 'C:/ASM/DevData/learning/tensorflow/cnn_model'
sess = tf.Session()

loader = tf.train.import_meta_graph(path+'/my-test-model.meta')
loader.restore(sess, tf.train.latest_checkpoint(path))

graph = tf.get_default_graph()
x = graph.get_tensor_by_name('x:0')
y = graph.get_tensor_by_name('y:0')
keep_prob = graph.get_tensor_by_name('keep_prob:0')
conv1_w = graph.get_tensor_by_name('conv_1/W:0')


# In[ ]:


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

