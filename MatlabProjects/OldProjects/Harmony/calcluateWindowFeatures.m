function f = calcluateWindowFeatures(d)
len = size(d, 2);
f=[];
for i = 1:len
    t = d(:,i);
    f = [f mean(t) std(t) rms(t) zcr(t)];
    if mod(i,3) == 0
        icrs =[ icr(d(:,i-2), d(:,i-1)), icr(d(:,i-1), d(:,i)), icr(d(:,i), d(:,i-2))];
        f = [f icrs];
    end
end

