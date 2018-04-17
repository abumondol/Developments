function res = getCDF(weights)
weights = weights*100;
res = zeros(101,1);
for i=1:length(weights)
    a = ceil(weights(i));    
    res(a+1,1) = res(a+1,1) + 1;
end

for i =2:length(res)
    res(i,1) = res(i,1) + res(i-1,1); 
end

res = res*100 / length(weights);
end
