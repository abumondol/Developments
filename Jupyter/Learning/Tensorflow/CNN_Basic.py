
# coding: utf-8

# In[3]:


import tensorflow as tf
from tensorflow.examples.tutorials.mnist import input_data


# In[6]:


mnist = input_data.read_data_sets('/tmp/data/', one_hot = True)


# In[7]:


#Training Parameters
learning_rate = 0.0001
num_steps = 50
batch_size = 128
display_step = 10

#Network Parameters
num_input = 784
num_classes = 10
dropout = 0.5


# In[10]:


#tf Graph input
X = tf.placeholder(tf.float32, [None, num_input])
Y = tf.placeholder(tf.float32, [None, num_classes])
keep_prob = tf.placeholder(tf.float32)


# In[31]:


def conv2d(x, W, b, strides=1):
    x = tf.nn.conv2d(x, W, strides = [1, strides, strides, 1], padding='SAME')
    x = tf.nn.bias_add(x, b)
    x = tf.nn.relu(x)
    return x

def maxpool2d(x, k=2):
    x = tf.nn.max_pool(x, ksize=[1,k,k,1], strides=[1,k,k,1], padding='SAME')
    return x

def conv_net(x, weights, biases, dropout):
    x = tf.reshape(x, shape=[-1, 28,28,1])
    conv1 = conv2d(x, weights['wc1'], biases['bc1'])
    conv1 = maxpool2d(conv1, k=2)
    
    conv2 = conv2d(conv1, weights['wc2'], biases['bc2'])
    conv2 = maxpool2d(conv2, k=2)
    
    fc1 = tf.reshape(conv2, [-1, weights['wd1'].get_shape().as_list()[0]])
    fc1 = tf.add(tf.matmul(fc1, weights['wd1']), biases['bd1'])
    fc1 = tf.nn.relu(fc1)
    fc1 = tf.nn.dropout(fc1, dropout)
    
    out = tf.add(tf.matmul(fc1, weights['out']), biases['out'])
    
    return out


# In[33]:


weights = {
    #5x5 conv, 1 input, 32 output
    'wc1' : tf.Variable(tf.random_normal([5, 5, 1, 32])),
    
    #5x5 conv, 32 input, 64 output
    'wc2': tf.Variable(tf.random_normal([5, 5, 32, 64])),
    
    #fully connected, 7*7*64 inputs, 1024 outputs
    'wd1': tf.Variable(tf.random_normal([7*7*64, 1024])),
    
    #1024 inputs, 10 outputs (class prediction)
    'out': tf.Variable(tf.random_normal([1024, num_classes]))    
}

biases={
    'bc1':tf.Variable(tf.random_normal([32])),
    'bc2':tf.Variable(tf.random_normal([64])),
    'bd1':tf.Variable(tf.random_normal([1024])),
    'out':tf.Variable(tf.random_normal([num_classes]))
}

#Construct model
logits = conv_net(X, weights, biases, keep_prob)
prediction = tf.nn.softmax(logits)

#Define loss and optimizer
loss_op = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(logits=logits, labels=Y))
optimizer = tf.train.AdamOptimizer(learning_rate)
train_op = optimizer.minimize(loss_op)

#Evaluate model
correct_pred = tf.equal(tf.argmax(prediction, 1), tf.argmax(Y,1))
accuracy = tf.reduce_mean(tf.cast(correct_pred, tf.float32))

#Initialize variables
init = tf.global_variables_initializer()


# In[ ]:


with tf.Session() as sess:
    sess.run(init)
    
    for step in range(1, num_steps+1):
        batch_x, batch_y = mnist.next_batch(batch_size)
        sess.run(train_op, feed_dict={X:batch_x, Y:batch_y, keep_prob:dropout})
        
        if step%display_step==0 or step==1:
            loss, acc = sess.run([loss_op, accuracy], feed_dict{={X:batch_x, 
                                                                  Y=batch_y, 
                                                                  keep_prob:dropout}})
            


