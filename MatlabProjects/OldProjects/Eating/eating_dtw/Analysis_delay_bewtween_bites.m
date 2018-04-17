delays = [];
for i=1:36
    d = data(i).annots(:, 1);
    d = d(2:end) - d(1:end-1);
    delays = [delays; d];    
end

delays = delays(delays<20);
%delays = sort(delays);

hist(delays, 100)