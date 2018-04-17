

clear; load('features');
f = features;
features2 = [];
for subj=1:4
    for sess = 1:6
        xy = features{subj, sess}.xy;
        r = size(xy,1);
        features2 = [features2; zeros(r,1)+subj, zeros(r,1)+sess, xy];
    end
end

save('features2', 'features2');




return
%%%%%%%%%%%%%%
load('features');
f = features;
f = f(f(:,2)<=5, end);
for i = 0:4
    s = sum(f==i);
    fprintf('%d: %d\n', i, s);
    
    
end