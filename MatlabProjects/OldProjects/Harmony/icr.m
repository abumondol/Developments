function res = icr(x,y)
len = size(x,1);
count = 0;
for i = 1:len-1
    if x(i) >= y(i) && x(i+1) < y(i+1) || x(i) <= y(i) && x(i+1) > y(i+1)
        count = count + 1;
    end    
end
res = count/len;