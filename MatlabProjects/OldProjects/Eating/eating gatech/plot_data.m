acl = raw_data(3).acl;
annots = raw_data(3).annotations;
annots = annots(annots(:,2)>0, :);

count = length(annots)
for i = 1:count
    t = annots(i,1);
    label = annots(i,2);
    d = acl(acl(:,1)>=t-3 & acl(:,1)<=t+3, 2:4);    
    d = smooth(d, 0.8);
    mag = sqrt(sum(d.*d, 2));
    d = [d, mag];
    
    mid = round(length(d)/2, 0);
    figure 
    plot(d(:,1));
    hold on
    grid on
    plot(d(:,2));
    plot(d(:,3));
    plot(d(:,4));    
    legend('X','Y','Z', 'M')   
    
    waitforbuttonpress
    close all
end
