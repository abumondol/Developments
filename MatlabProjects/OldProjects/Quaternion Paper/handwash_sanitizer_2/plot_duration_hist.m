d = [];
for s =1:10
    d = [d; data(s).annot_times];    
end
d = [d(:, 2) - d(:,1)];
d = [d;d];
d = sortrows(d);
d(end-1) = 15;
d = d
figure
hist(d)
xlabel('Duration (second)')
ylabel('Count')