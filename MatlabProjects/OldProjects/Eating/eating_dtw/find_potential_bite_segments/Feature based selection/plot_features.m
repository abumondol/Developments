Xpos = [];
Xneg = [];
for s=1:36
    X = features(s).X;
    Y = features(s).Y;
    Xpos = [Xpos; X(Y==1, :)];
    Xneg = [Xneg; X(Y==0, :)];
end

close all
for i = 1: 3
    figure
    hist(Xpos(:,i), 1000);
    figure
    hist(Xneg(:,i), 1000);    
end



