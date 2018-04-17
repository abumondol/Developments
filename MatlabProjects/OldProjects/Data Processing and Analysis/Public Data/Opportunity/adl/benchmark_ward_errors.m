function output = benchmark_ward_errors(data)
% This function is a modification of the function plot_mset_errors
% developed by Jamie Ward et al.
%--------------------------------------------------------------------------
% The inputs are: labels and the data for which the accuracies have to be
% calculated
% The outputs are:
%   handle: handle for the figure plotted by the function
%   output: an array of various accuracy measures

nSubj=size(data,1);
nSets=size(data, 2);

for iSubj=1:nSubj
    for iSet=1:nSets % Gather all the data
        
        tm=data{iSubj,iSet};
        % Sum up times over all files
        nFiles = length(tm);
        tID=0;tOD=0;tMD=0;tIU=0;tOU=0;tIF=0;tConf=0;tT=0;
        for i=1:nFiles
            tOD = tOD+tm(i).OD;
            tID = tID+tm(i).ID;
            tMD = tMD+tm(i).MD;
            tIU = tIU+tm(i).IU;
            tOU = tOU+tm(i).OU;
            tIF = tIF+tm(i).IF;
            tConf = tConf + tm(i).Conf;
            tT = tT + tm(i).T;
        end
        
        nClasses=length(tT);
        Ngrp=[1]; Pgrp=[2:nClasses];
        
        % w.r.t. Null
        Del(iSet) = sum( [sum(tID(Ngrp,Pgrp)) sum(tOD(Ngrp,Pgrp)) sum(tMD(Ngrp,Pgrp))] );
        Und(iSet) = sum( [sum(tIU(Ngrp,Pgrp)) sum(tOU(Ngrp,Pgrp))] );
        Fra(iSet) = sum(  sum(tIF(Ngrp,Pgrp)) );
        Ins(iSet) = sum( [sum(sum(tID(Pgrp,Ngrp))) sum(sum(tIU(Pgrp,Ngrp))) sum(sum(tIF(Pgrp,Ngrp)))] );
        Ove(iSet) = sum( [sum(sum(tOD(Pgrp,Ngrp))) sum(sum(tOU(Pgrp,Ngrp)))] );
        Mer(iSet) = sum(  sum(sum(tMD(Pgrp,Ngrp))) );
        % Substitutions
        sID(iSet) = sum(sum(tID(Pgrp,Pgrp)));
        sIU(iSet) = sum(sum(tIU(Pgrp,Pgrp)));
        sIF(iSet) = sum(sum(tIF(Pgrp,Pgrp)));
        sOD(iSet) = sum(sum(tOD(Pgrp,Pgrp)));
        sOU(iSet) = sum(sum(tOU(Pgrp,Pgrp)));
        sMD(iSet) = sum(sum(tMD(Pgrp,Pgrp)));
        % Correct
        I = eye(nClasses,nClasses);%identity matrix
        C = sum(tConf() .* I);%sum of diagonal of confusion martix
        
        pC(iSet) = sum(C(Pgrp));
        nC(iSet) = sum(C(Ngrp));
        T(iSet) = sum(tT);
        Pos(iSet)= sum(tT(Pgrp));
        Neg(iSet)= sum(tT(Ngrp));
        
        sub(iSet)= sum(sum(tConf(Pgrp,Pgrp))-C(Pgrp));
        predPos(iSet)= sum(sum(tConf(Pgrp,:)));
        predNeg(iSet)= sum(sum(tConf(Ngrp,:)));
        
    end
    
    subst= sID+sIU+sIF+sOD+sOU+sMD;
    
    % columns of M is stacked into a bar
    % there are as many bars as the rows of M
    %M(:,9)= pC;
    M(:,8)= pC;%nC;
    M(:,7)= Ove;
    M(:,6)= Und;
    M(:,5)= Mer;
    M(:,4) = Ins;
    M(:,3)= Fra;
    M(:,2) = Del;
    M(:,1)= subst;
    
    M = M(:, 1:8);    
    Mscaled = bsxfun(@rdivide,M,sum(M,2))*100;
    output{iSubj} = Mscaled;

end