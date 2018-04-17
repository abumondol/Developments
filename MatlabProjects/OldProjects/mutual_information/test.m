x = rand(1,1000)
y = x;
[estimate,Nbias,sigma,descriptor] = information(x,y)