
intervals = [];
for subj=1:4
    for sess = 1:5        
        ix = indices(indices(:,1)==subj & indices(:,2)==sess, 5);
        ix = ix(2:end,:) - ix(1:end-1,:);
        intervals = [intervals;ix];
    end
end

intervals = intervals/30;
figure
hist(intervals, 100);
xlabel('Bite/sip Interval (sec)')
title('Histogram');

intervals = intervals(intervals<10,:);
figure
hist(intervals, 100);
xlabel('Bite/sip Interval (sec)')
title('Histogram');