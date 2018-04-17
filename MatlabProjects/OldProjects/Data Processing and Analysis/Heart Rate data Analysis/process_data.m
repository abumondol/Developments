
t = [data.start_time];
mt = min(t);
for i=1:6    
    data(i).offset = (data(i)-mt)/1000;
    data(i)
    hr(:,1) = hr(:,1) - hr(1,1);
    hr(:,1) = hr(:,1)/1e9;    
    data(i).hr = hr(:, 1:4);    
end

save('data', 'data');