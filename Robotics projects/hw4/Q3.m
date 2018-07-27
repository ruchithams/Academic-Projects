% In this question, you need to pretrain on the MNIST digits and output the
% network layers for finetuning on the letters dataset. I've already
% included the code that loads the MNIST data. You need to actually do the
% training using "trainNetwork", get the resulting weights, and create a
% new network for training on the letters that is "preloaded" with the
% weights from the MNIST training session. This is an application of
% transfer learning. You should refer to the code in hw5.m and the discussion 
% on transfer learning in the matlab docs here:
% https://www.mathworks.com/help/nnet/examples/transfer-learning-using-alexnet.html
% 
% input: Null
% output: a matlab "Layer" object. For more info, see:
%         https://www.mathworks.com/help/nnet/ref/nnet.cnn.layer.layer.html
%
function layers = Q3()

    % Load MNIST digit dataset
    digitDatasetPath = fullfile(matlabroot,'toolbox','nnet','nndemos',...
        'nndatasets','DigitDataset');
    digitData = imageDatastore(digitDatasetPath,...
        'IncludeSubfolders',true,'LabelSource','foldernames');
    trainNumFiles = 750;
    [trainDigitData,~] = splitEachLabel(digitData,trainNumFiles,'randomize');

    oldLayer = Q1();
    layerTransfer = oldLayer(1:end-3);
    layers = [layerTransfer
              fullyConnectedLayer(26,'WeightLearnRateFactor',2,'BiasLearnRateFactor',2)
              softmaxLayer
              classificationLayer];
    
end
